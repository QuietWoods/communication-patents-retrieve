package com.icdd.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class createXML {

	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		writeDemo();

	}

	private static void writeDemo() throws FactoryConfigurationError, XMLStreamException, FileNotFoundException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream("demo.xml"));
		writer.writeStartDocument("utf-8","1.0");
		
		writer.writeStartElement("patent");
		
		writer.writeAttribute("name", "mypatent");
		
		writer.writeStartElement("title");
		writer.writeCharacters("this is a");
		writer.writeCharacters("title");
		writer.writeEndElement();
		
		writer.writeStartElement("claim");
		writer.writeAttribute("claims", "any");
		writer.writeCharacters("this");
		writer.writeCharacters("is");
		writer.writeCharacters("a");
		writer.writeCharacters("claim");
		writer.writeEndElement();

		writer.writeEndElement();
		writer.writeEndDocument();

		writer.flush();
		writer.close();
	}

}