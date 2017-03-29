package com.icdd.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.junit.Test;

import com.icdd.lucence.SearchFiles;
import com.icdd.xml.ParserXML;

public class TestLucence {
//	@Test
//	public void indexFiles(){
//		String indexPath = "f:/download/index/";
//		String docsPath = "f:/download/source";
//		IndexFiles indexTest = new IndexFiles(indexPath, docsPath);
//		indexTest.index(false);
//	}
//	@Test
//	public void searchFiles(){
//		SearchFiles searchTest = new SearchFiles();
//		searchTest.searchFiles();
//	}
	@Test
	public void searchFilesWeb(){
		SearchFiles searchTest = new SearchFiles();
		List<Document> docs = new ArrayList<Document>();
		docs = searchTest.searchFilesWeb("大桥未久");
		ParserXML px = new ParserXML();
		
		int i = 0;
		for (Document item : docs) {
			i++;
			System.out.println(item.get("path"));
			px.readDataSimple(new File(item.get("path")));
			if(i > 3)
				break;
		}
	}
}


