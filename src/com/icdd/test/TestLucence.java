package com.icdd.test;

import org.junit.Test;


import com.icdd.lucence.IndexFiles;
import com.icdd.lucence.SearchFiles;

public class TestLucence {
	@Test
	public void indexFiles(){
		IndexFiles indexTest = new IndexFiles();
		indexTest.index();
	}
	@Test
	public void searchFiles(){
		SearchFiles searchTest = new SearchFiles();
		searchTest.searchFiles();
	}
}


