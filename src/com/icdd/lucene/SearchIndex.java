package com.icdd.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;

import com.icdd.xml.ParserSource;

public class SearchIndex extends IndexLucene {
	
	public SearchIndex() {
		//
	}



	/**
	 * search files by queryString.
	 * 
	 * @param queryString
	 * @return the list of documents
	 * @throws Exception
	 */
	public List<Document> searchFiles(String queryString, String field) {
		// the mark of printing the source file information of the index file.
		boolean raw = false;
		// 10 records per page.
		int hitsPerPage = 10;
		List<Document> docs = new ArrayList<Document>();
		IndexReader reader;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH)));

			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(field, ANALYZER);
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			String line = queryString;
			line = line.trim();
			if (line.length() == 0) {
				return docs;
			}
			System.out.println(line);
			Query query;
			try {
				query = parser.parse(line);

				logger.info("Searching for:" + query.toString(field));

				try {
					docs = doPagingSearch(docs, searcher, query, hitsPerPage, raw);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return docs;
	}
	public List<Document> searchFilesNotAnalyzer(String queryString, String field) {
		// the mark of printing the source file information of the index file.
		boolean raw = false;
		// 10 records per page.
		int hitsPerPage = 10;
		List<Document> docs = new ArrayList<Document>();
		IndexReader reader;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH)));

			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(field,SDANALYZER);
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			String line = queryString;
			line = line.trim();
			if (line.length() == 0) {
				return docs;
			}
			System.out.println(line);
			Query query;
			try {
				query = parser.parse(line);

				logger.info("Searching for:" + query.toString(field));

				try {
					docs = doPagingSearch(docs, searcher, query, hitsPerPage, raw);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return docs;
	}
	/**
	 * 全匹配
	 * @param queryString
	 * @param field
	 * @return
	 */
	public List<Document> searchFilesByTerm(String queryString, String field) {
		// the mark of printing the source file information of the index file.
		boolean raw = false;
		// 10 records per page.
		int hitsPerPage = 10;
		List<Document> docs = new ArrayList<Document>();
		IndexReader reader;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH)));

			IndexSearcher searcher = new IndexSearcher(reader);
			 Term t =new Term(field,queryString);
	          TermQuery query=new TermQuery(t);
			
			String line = queryString;
			line = line.trim();
			if (line.length() == 0) {
				return docs;
			}
			System.out.println(line);
			logger.info("Searching for:" + query.toString(field));

			try {
				docs = doPagingSearchByTerm(docs, searcher, query, hitsPerPage, raw);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return docs;
	}
	static List<Document> doPagingSearchByTerm(List<Document> docs, IndexSearcher searcher, TermQuery query, int hitsPerPage,
			boolean raw) throws Exception {
		// Collect enough documents to show 5 pages
		Date starttime = new Date();

		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		logger.info(numTotalHits + "total matching documents");
		Date endtime = new Date();
		logger.info("one search cost :" + (endtime.getTime() - starttime.getTime()) + "ms");

		logger.info("search results list：");
		for (ScoreDoc scoreDoc : hits) {// 获取查找的文档的属性数据

			int docID = scoreDoc.doc;
			String score = String.valueOf(scoreDoc.score);
			Document document = searcher.doc(docID);
			
			document.add(new StringField("score", score,Field.Store.YES));
			docs.add(document);
		}
		return docs;
	}
	static List<Document> doPagingSearch(List<Document> docs, IndexSearcher searcher, Query query, int hitsPerPage,
			boolean raw) throws Exception {
		// Collect enough documents to show 5 pages
		Date starttime = new Date();

		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		logger.info(numTotalHits + "total matching documents");
		Date endtime = new Date();
		logger.info("one search cost :" + (endtime.getTime() - starttime.getTime()) + "ms");

		logger.info("search results list：");
		 // 自定义标注高亮文本标签  
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter(  
                "<span class=\"highlight\">", "</span>");  
        
		QueryScorer scorer = new QueryScorer(query);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		Highlighter highlight = new Highlighter(htmlFormatter, scorer);
		highlight.setTextFragmenter(fragmenter);
		ParserSource px = new ParserSource();
		
		for (ScoreDoc scoreDoc : hits) {// 获取查找的文档的属性数据

			int docID = scoreDoc.doc;
			String score = String.valueOf(scoreDoc.score);
			Document document = searcher.doc(docID);
			File newFile = new File(document.get("path"));
			String value = px.readDataToStr(newFile);
			if (value != null) {
				TokenStream tokenStream = ANALYZER.tokenStream("contents", new StringReader(value));
				String str = highlight.getBestFragment(tokenStream, value);			
				//添加 搜索摘要
				document.add(new StringField("abst", str, Field.Store.YES));
			}
			document.add(new StringField("score", score,Field.Store.YES));
			docs.add(document);
		}
		return docs;
	}
}
