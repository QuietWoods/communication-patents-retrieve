package com.icdd.test;

import org.junit.Test;


import com.icdd.lucence.LucenceIndex;
import com.icdd.lucence.SearchFiles;

public class TestLucence {
	@Test
	public void indexFiles(){
		LucenceIndex indexTest = new LucenceIndex();
		indexTest.index();
	}
	@Test
	public void searchFiles(){
		SearchFiles searchTest = new SearchFiles();
		searchTest.searchFiles();
	}
}


