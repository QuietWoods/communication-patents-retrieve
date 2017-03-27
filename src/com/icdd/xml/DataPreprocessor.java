package com.icdd.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * 专利名、专利号、专利摘要、权利要求书提取出来，把无用的标签和内容过滤掉 use StAx parser
 * 
 * @author wl
 *
 */
public class DataPreprocessor {
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

			while (parser.hasNext()) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					// patent number
					if (parser.getLocalName().equals("application-reference")) {
						boolean flag = false;
						while (parser.hasNext() && flag == false) {
							event = parser.next();
							if (event == XMLStreamConstants.START_ELEMENT) {

								if (parser.getLocalName().equals("doc-number")) {
									// write doc-number start element
									writer.writeStartElement("doc-number");
									event = parser.next();
									if (event == XMLStreamConstants.CHARACTERS) {
										if (parser.isWhiteSpace() == false) {
											String number = parser.getText().trim();
											writer.writeCharacters(number);
											flag = true;
										}
									}
								}
							}
						}
						// write end patent number node
						writer.writeEndElement();
					}
				}

				if (event == XMLStreamConstants.START_ELEMENT) {
					// patent title
					if (parser.getLocalName().equals("invention-title")) {
						int attrsCount = parser.getAttributeCount();
						if (attrsCount == 0) {
							writer.writeStartElement("invention-title");
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS) {
								if (parser.isWhiteSpace() == false) {
									String title = parser.getText().trim();
									writer.writeCharacters(title);
								}
							}
						}
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT) {
					// patent abstract
					if (parser.getLocalName().equals("p")) {
						String numStr = parser.getAttributeValue(null, "num");
						if (numStr.equals("1")) {
							writer.writeEndElement();
							writer.writeStartElement("abstract");
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS) {
								if (parser.isWhiteSpace() != true) {
									String abstractStr = parser.getText().trim();
									writer.writeCharacters(abstractStr);
								}
							}
						}
					}
				}
				// description
				if (event == XMLStreamConstants.START_ELEMENT) {
					if (parser.getLocalName().equals("description")) {
						writer.writeEndElement();
						writer.writeStartElement("description");
						writer.writeStartElement("p");
						while (parser.hasNext()) {
							int inevent = parser.next();
							// 遇到p标签就换行
							if (inevent == XMLStreamConstants.START_ELEMENT)
								if (parser.getLocalName().equals("p")) {
									writer.writeEndElement();
									writer.writeStartElement("p");
								}
							if (inevent == XMLStreamConstants.END_ELEMENT) {
								if (parser.getLocalName().equals("description"))
									break;
							}
							if (inevent == XMLStreamConstants.CHARACTERS) {
								if (parser.isWhiteSpace() != true) {
									String description = parser.getText().trim();
									writer.writeCharacters(description);
								}
							}

						}
						writer.writeEndElement();
						writer.writeEndElement();
					}
				}
				// claims
				if (event == XMLStreamConstants.START_ELEMENT) {
					if (parser.getLocalName().equals("claims")) {
						writer.writeStartElement("claims");
						while (parser.hasNext()) {
							int inevent = parser.next();
							// 遇到claim标签就换行
							if (inevent == XMLStreamConstants.START_ELEMENT && parser.getLocalName().equals("claim"))

								writer.writeStartElement("p");
							if (inevent == XMLStreamConstants.END_ELEMENT) {
								if (parser.getLocalName().equals("claim"))
									writer.writeEndElement();
								if (parser.getLocalName().equals("claims"))
									break;
							}
							if (inevent == XMLStreamConstants.CHARACTERS) {
								if (parser.isWhiteSpace() != true) {
									String claims = parser.getText().trim();
									writer.writeCharacters(claims);
								}
							}

						}
						writer.writeEndElement();
					}
				}
	}
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();

		} catch (XMLStreamException e) {
			System.out.println("XMLStreamException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
