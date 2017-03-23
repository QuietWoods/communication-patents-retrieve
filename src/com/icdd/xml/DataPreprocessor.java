package com.icdd.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * 专利名、专利号、专利摘要、权利要求书提取出来，把无用的标签和内容过滤掉
 * use StAx parser
 * @author wl
 *
 */
public class DataPreprocessor {
	/**
	 * Determine the given file according to the classification of identifier
	 * belongs to the category
	 * @param file  the XML data file
	 * @param identity the class identifier 
	 * @return true or false
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public boolean classifierXML(File file, String identity) throws FileNotFoundException, XMLStreamException {
		InputStream in = new FileInputStream(file);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		// factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
		XMLStreamReader parser = factory.createXMLStreamReader(in, "utf-8");
		String classify = null;
		boolean result = false;
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
		return result;
	}
	/**
	 * XML transform to text file,extract doc_number, patent name, patent description and patent claims.
	 * @param file  a XML file
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public void xmlToText(File file) throws FileNotFoundException, XMLStreamException {
		String createFileName = file.getName().replaceAll(".XML", ".txt");
		System.out.println(createFileName);

		PrintWriter out = new PrintWriter(createFileName);

		InputStream in = new FileInputStream(file);
		System.out.println(file.getAbsolutePath());
		XMLInputFactory factory = XMLInputFactory.newInstance();
		// factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
		XMLStreamReader parser = factory.createXMLStreamReader(in, "utf-8");
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
								event = parser.next();
								if (event == XMLStreamConstants.CHARACTERS) {
									if (parser.isWhiteSpace() == false) {
										String number = parser.getText().trim();
										out.println(number);
										flag = true;
									}
								}
							}
						}
					}

				}
			}

			if (event == XMLStreamConstants.START_ELEMENT) {
				// patent title
				if (parser.getLocalName().equals("invention-title")) {
					int attrsCount = parser.getAttributeCount();
					if (attrsCount == 0) {
						event = parser.next();
						if (event == XMLStreamConstants.CHARACTERS) {
							if (parser.isWhiteSpace() == false) {
								String title = parser.getText().trim();
								out.println(title);
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
						event = parser.next();
						if (event == XMLStreamConstants.CHARACTERS) {
							if (parser.isWhiteSpace() != true) {
								String abstractStr = parser.getText().trim();
								out.println(abstractStr);
							}
						}
					}
				}
			}
			// description
			if (event == XMLStreamConstants.START_ELEMENT) {
				if (parser.getLocalName().equals("description")) {
					while (parser.hasNext()) {
						int inevent = parser.next();
						if (inevent == XMLStreamConstants.END_ELEMENT) {
							if (parser.getLocalName().equals("description"))
								break;
						}
						if (inevent == XMLStreamConstants.CHARACTERS) {
							if (parser.isWhiteSpace() != true) {
								String description = parser.getText().trim();
								out.println(description);
							}
						}

					}
				}
			}
			// claims
			if (event == XMLStreamConstants.START_ELEMENT) {
				if (parser.getLocalName().equals("claims")) {
					while (parser.hasNext()) {
						int inevent = parser.next();
						if (inevent == XMLStreamConstants.END_ELEMENT) {
							if (parser.getLocalName().equals("claims"))
								break;
						}
						if (inevent == XMLStreamConstants.CHARACTERS) {
							if (parser.isWhiteSpace() != true) {
								String claims = parser.getText().trim();
								out.println(claims);
							}
						}

					}
				}
			}
		}
		out.flush();
		out.close();
	}
}
