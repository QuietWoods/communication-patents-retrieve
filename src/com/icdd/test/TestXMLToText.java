package com.icdd.test;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.icdd.xml.XMLToText;

public class TestXMLToText {
	@Test
	public void xml2TextTest(){
		File source = new File("f:\\download\\source");
		File target = new File("f:\\download\\target");
		XMLToText xtt = new XMLToText(source, target,"H");
		long start = System.currentTimeMillis();
		
		xtt.xmlToTextRecursive();
		
		long end = System.currentTimeMillis();
		
		System.out.println((end - start) + " milliseconds");
	}
}
