package com.icdd.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class ParserSource {
	public static final Logger logger = (Logger) LogManager.getLogger("mylog");

	public String readDataForIndexTitle(File file) {
		logger.error("BEGIN readDataForIndexTitle:");
		long start = System.currentTimeMillis();
		// 初始化返回值
		String title = new String();
		StringBuilder sb = new StringBuilder();
		InputStream in;
		try {
			in = new FileInputStream(file);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in, "utf-8");
			while (parser.hasNext()) {
				int event = parser.next();
				// title
				if (event == XMLStreamConstants.START_ELEMENT && "invention-title".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						sb.append(parser.getText().trim());
						title = sb.toString();
						break;
					}
				}
			}
			long end = System.currentTimeMillis();
			logger.info("END readData:" + (end - start) + " milliseconds");

		} catch (XMLStreamException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return title;
	}

	public Map<String, String> readData(File file) {
		logger.error("BEGIN readDataForDetail:");
		long start = System.currentTimeMillis();
		// 初始化返回值
		Map<String, String> detail = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		InputStream in;
		try {
			in = new FileInputStream(file);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in, "utf-8");
			while (parser.hasNext()) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT && "doc-number".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						sb.append(parser.getText().trim());
						detail.put("number", sb.toString());
						sb = sb.delete(0, sb.length());
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "classification".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						sb.append(parser.getText().trim());
						detail.put("classification", sb.toString());
						sb = sb.delete(0, sb.length());
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "invention-title".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						sb.append(parser.getText().trim());
						detail.put("title", sb.toString());
						sb = sb.delete(0, sb.length());
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "abstract".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						sb.append(parser.getText().trim());
						detail.put("abstract", sb.toString());
						sb = sb.delete(0, sb.length());
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "description".equals(parser.getLocalName())) {

					while (parser.hasNext()) {
						event = parser.next();
						if(event == XMLStreamConstants.END_ELEMENT && "description".equals(parser.getLocalName())){
							break;
						}
						if(event == XMLStreamConstants.END_ELEMENT && "p".equals(parser.getLocalName())){
							sb.append("<br/>");
						}
						if (event == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())) {
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
								sb.append(parser.getText().trim());
								

							}

						}
					}
					if (detail.get("description") == null) {
						detail.put("description", sb.toString());
						sb = sb.delete(0, sb.length());
					}

				}

				if (event == XMLStreamConstants.START_ELEMENT && "claims".equals(parser.getLocalName())) {

					while (parser.hasNext()) {
						event = parser.next();
						if(event == XMLStreamConstants.END_ELEMENT && "claims".equals(parser.getLocalName())){
							break;
						}
						if (event == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())) {
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
								sb.append(parser.getText().trim());
								sb.append("<br/>");

							}
						}

					}
					if (detail.get("claims") == null) {
						detail.put("claims", sb.toString());
						sb = sb.delete(0, sb.length());
					}
				}
			}

			long end = System.currentTimeMillis();
			logger.info("END readDataForDetail:" + (end - start) + " milliseconds");

		} catch (XMLStreamException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return detail;
	}

	public Map<String, String> readDataFromResource(File file) {
		logger.error("BEGIN readDataForDetail:");
		long start = System.currentTimeMillis();
		// 初始化返回值
		Map<String, String> detail = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		InputStream in;
		try {
			in = new FileInputStream(file);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in, "utf-8");
			while (parser.hasNext()) {
				int event = parser.next();
				// application-reference
				if (event == XMLStreamConstants.START_ELEMENT
						&& "application-reference".equals(parser.getLocalName())) {
					event = parser.next();
					while (parser.hasNext()) {
						event = parser.next();
						if (event == XMLStreamConstants.END_ELEMENT && "doc-number".equals(parser.getLocalName())) {
							break;
						}
						if (event == XMLStreamConstants.START_ELEMENT && "doc-number".equals(parser.getLocalName())) {
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
								sb.append(parser.getText().trim());
								detail.put("number", sb.toString());
								sb = sb.delete(0, sb.length());
							}
						}
					}

				}
				if (event == XMLStreamConstants.START_ELEMENT && "classification-ipcr".equals(parser.getLocalName())) {
					while (parser.hasNext()) {
						int inevent = parser.next();

						if (inevent == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
							// sb.append(parser.getText().trim().substring(0,
							// 3));
							sb.append(parser.getText().trim());
							detail.put("classification", sb.toString());
							sb = sb.delete(0, sb.length());

						}
						if (inevent == XMLStreamConstants.END_ELEMENT && "text".equals(parser.getLocalName())) {
							break;
						}
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "invention-title".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						sb.append(parser.getText().trim());
						detail.put("title", sb.toString());
						sb = sb.delete(0, sb.length());
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "abstract".equals(parser.getLocalName())) {
					event = parser.next();
					while (parser.hasNext()) {
						event = parser.next();
						if (event == XMLStreamConstants.END_ELEMENT && "abstract".equals(parser.getLocalName())) {
							break;
						}
						if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
							sb.append(parser.getText().trim());

							detail.put("abstract", sb.toString());
							sb = sb.delete(0, sb.length());
						}
					}
				}

				if (event == XMLStreamConstants.START_ELEMENT && "description".equals(parser.getLocalName())) {

					while (parser.hasNext()) {
						event = parser.next();
						if (event == XMLStreamConstants.END_ELEMENT && "description".equals(parser.getLocalName())) {
							break;
						}
						if (event == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())) {
							// event = parser.next();
							while (parser.hasNext()) {
								event = parser.next();
								if (event == XMLStreamConstants.END_ELEMENT && "p".equals(parser.getLocalName())) {
									sb.append("<br />");
									break;
								}
								if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
									sb.append(parser.getText().trim());

								}
							}
						}

					}
					if (detail.get("description") == null) {
						detail.put("description", sb.toString());
						sb = sb.delete(0, sb.length());
					}
				}

				if (event == XMLStreamConstants.START_ELEMENT && "claims".equals(parser.getLocalName())) {

					while (parser.hasNext()) {
						int inevent = parser.next();
						// 遇到claim标签就换行
						if (inevent == XMLStreamConstants.START_ELEMENT && "claim".equals(parser.getLocalName())) {
						}

						if (inevent == XMLStreamConstants.END_ELEMENT) {
							if ("claim".equals(parser.getLocalName()))
								sb.append("<br />");
							if ("claims".equals(parser.getLocalName()))
								break;
						}
						if (inevent == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {

							sb.append(parser.getText().trim());

						}

					}
					if (detail.get("claims") == null) {
						detail.put("claims", sb.toString());
						sb = sb.delete(0, sb.length());
					}

				}

			}
			long end = System.currentTimeMillis();
			logger.info("END readDataForDetail:" + (end - start) + " milliseconds");

		} catch (XMLStreamException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return detail;
	}

	public void readDataSimple(File file) {
		System.out.println("begin readData:");
		InputStream in;
		try {
			in = new FileInputStream(file);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in, "utf-8");
			while (parser.hasNext()) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						String content = parser.getText().trim();
						System.out.println(content);
					}
				}
			}

			System.out.println("end readData:");

		} catch (XMLStreamException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public String readDataToStr(File file) {
		StringBuffer sb = new StringBuffer();
		InputStream in;
		try {
			in = new FileInputStream(file);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in, "utf-8");
			while (parser.hasNext()) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						String content = parser.getText().trim();
						sb.append(content);
					}
				}
			}

		} catch (XMLStreamException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return sb.toString();
	}
}
