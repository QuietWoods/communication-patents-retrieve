package com.icdd.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * 专利名、专利号、专利摘要、权利要求书提取出来，把无用的标签和内容过滤掉 use StAx parser
 * 
 * @author wl
 *
 */
public class DataPreprocessor {
	private static Logger logger = (Logger) LogManager.getLogger("mylog");
	/**
	 * Determine the given file according to the classification of identifier
	 * belongs to the category
	 * 
	 * @param file
	 *            the XML data file
	 * @param identity
	 *            the class identifier
	 * @return true or false
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public boolean isTeleXML(File file, String identity) {
		boolean result = false;
		InputStream in;
		try {
			in = new FileInputStream(file);

			XMLInputFactory factory = XMLInputFactory.newInstance();
			// factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
			XMLStreamReader parser;

			parser = factory.createXMLStreamReader(in, "utf-8");
			String classify = null;

			while (parser.hasNext()) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if (parser.getLocalName().equals("classification-ipcr")) {
						while (parser.hasNext()) {
							int inevent = parser.next();
							if (inevent == XMLStreamConstants.END_ELEMENT) {
								if (parser.getLocalName().equals("text"))
									break;
							}
							if (inevent == XMLStreamConstants.CHARACTERS) {
								if (parser.isWhiteSpace() != true) {
									classify = parser.getText().trim();
								}
							}

						}
						if (classify.indexOf(identity) == 0) {
							result = true;
							break;
						}
					}
				}

			}
		} catch (XMLStreamException e) {
			System.out.println("classify XMLStreamException: " + e.getMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("classify FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * XML transform to text file,extract doc_number, patent name, patent
	 * description and patent claims.
	 * 
	 * @param file
	 *            a XML file
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public void purifyXML(File file, File target) {
		// 目标文件名，与原文件同名
		String newFileName = file.getName();
		try {
			// xml 输出流
			XMLOutputFactory outfactory = XMLOutputFactory.newInstance();

			XMLStreamWriter writer = outfactory.createXMLStreamWriter(
					new FileOutputStream(target.getCanonicalPath() + File.separator + newFileName));
			// 读取原文件
			InputStream in = new FileInputStream(file);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			// factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
			XMLStreamReader parser;
			parser = factory.createXMLStreamReader(in, "utf-8");

			// begin write XML document
			writer.writeStartDocument("utf-8", "1.0");
			// write XML document root node
			writer.writeStartElement("cn-patent-document");
			String titleStr = "invention-title";
			while (parser.hasNext()) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT
						&& "application-reference".equals(parser.getLocalName())) {
					// patent number
					boolean flag = false;
					while (parser.hasNext() && flag == false) {
						event = parser.next();
						if (event == XMLStreamConstants.START_ELEMENT && "doc-number".equals(parser.getLocalName())) {

							// write doc-number start element
							writer.writeStartElement("doc-number");
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
								String number = parser.getText().trim();
								writer.writeCharacters(number);
								flag = true;
							}

						}
					}
					// write end patent number node
					writer.writeEndElement();
				}

				if (event == XMLStreamConstants.START_ELEMENT && titleStr.equals(parser.getLocalName())) {
					// patent title
					int attrsCount = parser.getAttributeCount();
					if (attrsCount == 0) {
						writer.writeStartElement(titleStr);
						event = parser.next();
						if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
							String title = parser.getText().trim();
							writer.writeCharacters(title);
						}
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())) {
					// patent abstract
					String numStr = parser.getAttributeValue(null, "num");
					if ("1".equals(numStr)) {
						writer.writeEndElement();
						writer.writeStartElement("abstract");
						event = parser.next();
						if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
							String abstractStr = parser.getText().trim();
							writer.writeCharacters(abstractStr);
						}
					}
				}
				// description
				if (event == XMLStreamConstants.START_ELEMENT && "description".equals(parser.getLocalName())) {
					writer.writeEndElement();
					writer.writeStartElement("description");
					writer.writeStartElement("p");
					while (parser.hasNext()) {
						int inevent = parser.next();
						// 遇到p标签就换行
						if (inevent == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())){
								writer.writeEndElement();
								writer.writeStartElement("p");
							}
						if (inevent == XMLStreamConstants.END_ELEMENT && "description".equals(parser.getLocalName())) {
								break;
						}
						if (inevent == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
							String description = parser.getText().trim();
							writer.writeCharacters(description);
						}

					}
					writer.writeEndElement();
					writer.writeEndElement();

				}
				// claims
				if (event == XMLStreamConstants.START_ELEMENT && "claims".equals(parser.getLocalName())) {

					writer.writeStartElement("claims");
					while (parser.hasNext()) {
						int inevent = parser.next();
						// 遇到claim标签就换行
						if (inevent == XMLStreamConstants.START_ELEMENT && "claim".equals(parser.getLocalName()))

							writer.writeStartElement("p");
						if (inevent == XMLStreamConstants.END_ELEMENT) {
							if ("claim".equals(parser.getLocalName()))
								writer.writeEndElement();
							if ("claims".equals(parser.getLocalName()))
								break;
						}
						if (inevent == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {

							String claims = parser.getText().trim();
							writer.writeCharacters(claims);

						}

					}
					writer.writeEndElement();

				}
			}
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();

		} catch (XMLStreamException e) {
			logger.error("XMLStreamException: " + e.getMessage());			
		} catch (IOException e) {
			logger.error("IOException: " + e.getMessage());			
		}
	}

}
