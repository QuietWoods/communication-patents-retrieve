package com.icdd.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ParserXML {
	public void readData(File file) {
		System.out.println("begin readData:");
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
						String number = parser.getText().trim();
						System.out.println(number);
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "invention-title".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						String title = parser.getText().trim();
						System.out.println(title);
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "abstract".equals(parser.getLocalName())) {
					event = parser.next();
					if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
						String title = parser.getText().trim();
						System.out.println(title);
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "description".equals(parser.getLocalName())) {
					event = parser.next();
					while (parser.hasNext()) {
						event = parser.next();
						if (event == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())) {
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
								String descContent = parser.getText().trim();
								System.out.println(descContent);
							}
						}
					}
				}
				if (event == XMLStreamConstants.START_ELEMENT && "claims".equals(parser.getLocalName())) {
					event = parser.next();
					while (parser.hasNext()) {
						
						event = parser.next();
						if (event == XMLStreamConstants.START_ELEMENT && "p".equals(parser.getLocalName())) {
							event = parser.next();
							if (event == XMLStreamConstants.CHARACTERS && !parser.isWhiteSpace()) {
								String descContent = parser.getText().trim();
								System.out.println(descContent);
							}
						}
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
}
