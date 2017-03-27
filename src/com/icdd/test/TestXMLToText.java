package com.icdd.test;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

import com.icdd.xml.XMLToText;

public class TestXMLToText {
	private static Logger logger = (Logger) LogManager.getLogger("mylog");
	@Test
	public void xml2TextTest(){
		File source = new File("f:\\download\\source");
		File target = new File("f:\\download\\target");
		XMLToText xtt = new XMLToText(source, target,"H");
		long start = System.currentTimeMillis();
		logger.error("begin: ");
		xtt.xmlToTextRecursive();
		
		long end = System.currentTimeMillis();
		
		logger.error((end - start) + " milliseconds");
	}
}
