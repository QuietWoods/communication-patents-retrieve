package com.icdd.test;

import org.junit.Test;

import com.icdd.lucene.QueryToCategory;

public class TestStAx {
	// @Test
	// public void dataPreprocessor() throws FileNotFoundException,
	// XMLStreamException{
	// DataPreprocessor st = new DataPreprocessor();
	// File file = new File("201080035470NEW.XML");
	// // st.xmlToText(file);
	// long start = System.currentTimeMillis();
	// if (st.isTeleXML(file))
	// System.out.println("true");
	// else
	// System.out.println("false");
	// long end = System.currentTimeMillis();
	// System.out.println((end - start) + " milliseconds");
	// }
//	@Test
//	public void readXML() {
//		// ParserXML px= new ParserXML();
//		ParserSource px = new ParserSource();
//		File file = new File("F:\\研一课程\\信息检索与搜索引擎\\CN-TXTO-10-A_中国发明专利申请公布全文文本数据\\201180075618NEW.XML");
//		 
//
//		Map<String, String> results = new HashMap<>();
//
//		results = px.readData(file);
//		for (Map.Entry<String, String> entry : results.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			System.out.println(key + ": " + value);
//		}
//	}
@Test
public void getNonHan(){
	String rt =new  QueryToCategory("伙是同一H01L和悄youaremythish01l").getResult();
	System.out.println(rt);
}
}
