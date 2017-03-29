package com.icdd.test;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.icdd.xml.DataPreprocessor;
import com.icdd.xml.ParserXML;

public class TestStAx {
//	@Test
//	public void dataPreprocessor() throws FileNotFoundException, XMLStreamException{
//		DataPreprocessor st = new DataPreprocessor();
//		File file = new File("201080035470NEW.XML");
//		// st.xmlToText(file);
//		long start = System.currentTimeMillis();
//		if (st.isTeleXML(file))
//			System.out.println("true");
//		else
//			System.out.println("false");
//		long end = System.currentTimeMillis();
//		System.out.println((end - start) + " milliseconds");
//	}
	@Test
	public void readXML(){
		ParserXML px= new ParserXML();
		File file = new File("f:\\download\\target\\201110461664NEW.XML");
		px.readData(file);
	}
	
}
