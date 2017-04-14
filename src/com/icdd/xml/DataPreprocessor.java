package com.icdd.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
	private static Map<String, String> identify = new HashMap<>();
	private static Logger logger = (Logger) LogManager.getLogger("mylog");
	private static ParserSource px = new ParserSource();

	// 初始化块
	static {
		identify.put("H", "电学");
		identify.put("H01", "基本电气元件");
		identify.put("H02", "发电、变电或配电");
		identify.put("H03", "基本电子电路");
		identify.put("H04", "电通信技术");
		identify.put("H05", "其他类目不包含的电技术");
		identify.put("H99", "本部中其他类目不包括的技术主题");
	}

	public static Map<String, String> getIdentify() {
		return identify;
	}

	public static void setIdentify(Map<String, String> identify) {
		DataPreprocessor.identify = identify;
	}

	/**
	 * Determine the given file according to the classification of identifier
	 * belongs to the category
	 * 
	 * @param file
	 *            the XML data file
	 * @return true or false
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public boolean isTeleXML(File file) {
		boolean result = false;
		InputStream in;
		try {
			in = new FileInputStream(file);

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser;

			parser = factory.createXMLStreamReader(in, "utf-8");
			String classify = null;

			while (parser.hasNext()) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT && "classification-ipcr".equals(parser.getLocalName())) {
					while (parser.hasNext()) {
						int inevent = parser.next();

						if (inevent == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
							classify = parser.getText().trim();
							classify =classify.substring(0, 3);
						}
						if (inevent == XMLStreamConstants.END_ELEMENT && "text".equals(parser.getLocalName())) {
							break;
						}

					}
					if (identify.containsKey(classify)) {
						result = true;
						break;
					}

				}

			}
		} catch (XMLStreamException e) {
			logger.info("classify XMLStreamException: " + e.getMessage());

		} catch (FileNotFoundException e) {
			logger.info("classify FileNotFoundException: " + e.getMessage());

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
		// 目标文件名格式：原文件同名_专利标题。  
		String newFileName = file.getName().replace(".XML", "")+"_"+px.readDataForIndexTitle(file)+".xml";
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
				//classification
				if (event == XMLStreamConstants.START_ELEMENT
						&& "classifications-ipcr".equals(parser.getLocalName())) {
					// patent classification
					boolean flag = false;
					while (parser.hasNext() && flag == false) {
						event = parser.next();
						if (event == XMLStreamConstants.START_ELEMENT && "text".equals(parser.getLocalName())) {

							// write classification start element
							writer.writeStartElement("classification");
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
								String classify = parser.getText().trim();
								writer.writeCharacters(classify);
								flag = true;
							}

						}
					}
					// write end patent classification node
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
						if (inevent == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())) {
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
