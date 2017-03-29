package com.icdd.test;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

import com.icdd.xml.XMLHandler;

public class TestXMLToText {
	private static Logger logger = (Logger) LogManager.getLogger("mylog");
	@Test
	public void xml2TextTest(){
		File source = new File("F:\\研一课程\\信息检索与搜索引擎\\CN-TXTO-10-A_中国发明专利申请公布全文文本数据");
		//File source = new File("F:\\download\\source");
		File target = new File("f:\\download\\target");
		XMLHandler xtt = new XMLHandler(source, target);
		long start = System.currentTimeMillis();
		logger.error("begin: ");
		xtt.xmlsFormat();
		
		long end = System.currentTimeMillis();
		
		logger.error((end - start) + " milliseconds");
	}
}
