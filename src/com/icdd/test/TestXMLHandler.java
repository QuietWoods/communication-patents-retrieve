package com.icdd.test;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

import com.icdd.xml.XMLHandler;

public class TestXMLHandler {
	private static Logger logger = (Logger) LogManager.getLogger("mylog");
	/**
	 * xmlHandlerTest 从原始文件中提取出H部的专利文献，并过滤其他无用的标签，生成干净的专利文献文档。
	 */
	@Test
	public void xmlHandlerTest(){
		File source = new File("F:\\研一课程\\信息检索与搜索引擎\\CN-TXTO-10-A_中国发明专利申请公布全文文本数据");
		//File source = new File("F:\\download\\source");
		File target = new File("f:\\download\\source");
		XMLHandler xtt = new XMLHandler(source, target);
		long start = System.currentTimeMillis();
		logger.error("begin: ");
		xtt.xmlsFormat();
		
		long end = System.currentTimeMillis();
		
		logger.error((end - start) + " milliseconds");
	}
}
