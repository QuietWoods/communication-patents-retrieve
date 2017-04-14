package com.icdd.test;

import java.util.List;

import org.apache.lucene.document.Document;
import org.junit.Test;

import com.icdd.lucene.CreateIndex;
import com.icdd.lucene.SearchIndex;

public class TestIndexLucene {
	/**
	 * f:\download\source文件夹中的H部专利文件生成索引到f:\download\index文件夹中。
	 */
	/*@Test
	 public void creatIndex() {
	 CreateIndex il = new CreateIndex(false);
	 il.createOrUpdateIndex("F:\\download\\source");
	
	 }*/
	@Test
	public void searchFiles() {
		SearchIndex il2 = new SearchIndex();
		//List<Document> docs1 = il2.searchFiles("\"H02J 15/00 (2006.01)\"", "contents");
		//List<Document> docs2 = il2.searchFilesByTerm("接收设备、接收方法、发射设备和发射方法", "title");
		//List<Document> docs3 = il2.searchFilesNotAnalyzer("H02J 15", "contents");
		
	}

//	@Test
//	public void getDetail(File file) {
//		ParserSource px = new ParserSource();
//		Map<String, String> results = px.readData(file);
//		for (Map.Entry<String, String> entry : results.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			System.out.println(key + ": " + value);
//		}
//	}
	// @Test
	// public void deleteFile(){
	// IndexLucene il = new IndexLucene();
	// System.out.println("before delete:");
	// searchFiles(il);
	// System.out.println("可以回滚的删除：");
	//
	// //indexdel.deleteRollback("path", "201480001562NEW.XML");
	// //indexdel.delete("具有多个光源的运动传感器装置");
	// il.delete("201480001576.8","contents");
	// System.out.println("删除后：");
	// searchFiles(il);
	//
	// // il.submit();
	// //il.close();
	// //searchFiles();
	//// System.out.println("强制删除后：");
	// il.forceDelete();
	// il.submit();
	//// searchFilesWeb();
	// System.out.println("回滚后：");
	// il.rollback();
	// searchFiles(il);
	// il.close();
	// }
}
