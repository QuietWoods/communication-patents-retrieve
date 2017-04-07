/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icdd.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.icdd.xml.ParserXML;

public class IndexLucene {
	protected static final Logger logger = (Logger) LogManager.getLogger("mylog");
	protected static final FileFilter filter = new xmlFileFilter();
	protected static final String INDEX_PATH = "f:\\download\\indexs";
	protected static final String DOCS_PATH = "f:\\download\\source";
	protected static IndexWriter writer = null;
	// private static final Analyzer ANALYZER = new StandardAnalyzer();
	protected static final Analyzer ANALYZER = new SmartChineseAnalyzer();
	protected static Directory dir = null;
	protected static IndexSearcher searcher = null;
	static {
		try {
			dir = FSDirectory.open(Paths.get(INDEX_PATH));
			IndexWriterConfig iwc = new IndexWriterConfig(ANALYZER);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			logger.error("IOException: " + e.getMessage());
		}

	}

	/**
	 * create or update index files
	 */
	public void createOrUpdateIndex() {
		final Path docDir = Paths.get(DOCS_PATH);
		if (!Files.isReadable(docDir)) {
			logger.warn("Document directory '" + docDir + "'does not exist or  is not readable, "
					+ "please check the path");
		}
		Date start = new Date();
		try {
			logger.warn("Indexing to directory '" + INDEX_PATH + "'...");

			indexDocs(writer, docDir);

			Date end = new Date();
			logger.info(end.getTime() - start.getTime() + " total milliseconds");
		} catch (IOException e) {
			logger.info(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
					try {
						indexDoc(writer, file, attr.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
						logger.info(ignore.getMessage());
					}
					return FileVisitResult.CONTINUE;
				}
			});

		} else {

			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		// filter non-xml files
		if (filter.accept(file.toFile())) {
			try (InputStream stream = Files.newInputStream(file)) {
				// make a new,empty document
				Document doc = new Document();

				Field pathField = new StringField("path", file.toString(), Field.Store.YES);
				doc.add(pathField);
				doc.add(new StringField("name", file.getFileName().toString(), Field.Store.YES));
				doc.add(new SortedNumericDocValuesField("modified", lastModified));
				doc.add(new TextField("contents",
						new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

				if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
					// New index, so we just add the document (no old document
					// can
					// be there):
					logger.info("adding " + file);
					writer.addDocument(doc);
				} else {
					// Existing index (an old copy of this document may have
					// been
					// indexed) so
					// path, if present:
					logger.info("updating " + file);
					writer.updateDocument(new Term("path", file.toString()), doc);
				}
			}
		}
	}

	/**
	 * delete index by item.
	 * 
	 * @param item
	 * @param itemStr
	 */
	public void delete(String itemStr, String item) {
		try {
			writer.deleteDocuments(new Term(item, itemStr));
		} catch (IOException e) {
			logger.error("delete failed!");

		}
	}

	/**
	 * delete index by queries
	 * 
	 * @param queries
	 */
	public void delete(String queries) {
		QueryParser parser = new QueryParser("contents", ANALYZER);
		Query query;
		try {
			query = parser.parse(queries);
			writer.deleteDocuments(query);
		} catch (ParseException e) {
			logger.error("parse exception: " + e.getMessage());
		} catch (IOException e) {
			logger.error("delete documents failed: " + e.getMessage());
		}
	}

	/** force delete，merge index field **/
	public void forceDelete() {
		try {
			writer.forceMergeDeletes();
		} catch (IOException e) {
			logger.error("IOException failed: " + e.getMessage());
		}
	}

	/**
	 * search files by queryString.
	 * 
	 * @param queryString
	 * @return the list of documents
	 */
	public List<Document> searchFiles(String queryString, String field) {
		int repeat = 0;
		// the mark of printing the source file information of the index file.
		boolean raw = false;
		// 10 records per page.
		int hitsPerPage = 10;
		List<Document> docs = new ArrayList<Document>();

		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH)));
			searcher = new IndexSearcher(reader);
			//Analyzer analyzer = new s();

			QueryParser parser = new QueryParser(field, ANALYZER);
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			String line = queryString;
			line = line.trim();
			if (line.length() == 0) {
				return docs;
			}
			System.out.println(line);
			try {
				Query query = parser.parse(line);
				logger.info("Searching for:" + query.toString(field));

				if (repeat > 0) { // repeat & time as benchmark
					Date start = new Date();
					for (int i = 0; i < repeat; i++) {
						searcher.search(query, 100);
					}
					Date end = new Date();
					logger.info("Time: " + (end.getTime() - start.getTime()) + "ms");
				}

				try {
					docs = doPagingSearch(docs, searcher, query, hitsPerPage, raw);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (ParseException e) {

				e.printStackTrace();
			}
			reader.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
		return docs;
	}

	private List<Document> doPagingSearch(List<Document> docs, IndexSearcher searcher, Query query, int hitsPerPage,
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

		QueryScorer scorer = new QueryScorer(query);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		Highlighter highlight = new Highlighter(scorer);
		highlight.setTextFragmenter(fragmenter);
		ParserXML px = new ParserXML();
		int seq = 0;
		for (ScoreDoc scoreDoc : hits) {// 获取查找的文档的属性数据
			seq++;
			int docID = scoreDoc.doc;
			Document document = searcher.doc(docID);
			String str = "序号：" + seq + ",ID:" + document.get("id") + ",路径：" + document.get("path") + ",文件名："
					+ document.get("name") + "，内容：";

			String value = px.readDataToStr(new File(document.get("path")));
			if (value != null) {
				TokenStream tokenStream = ANALYZER.tokenStream("contents", new StringReader(value));
				String str1 = highlight.getBestFragment(tokenStream, value);
				str = str + str1;
			}
			System.out.println("查询出文件:" + str);
		}
		return docs;
	}

	/*
	 * roll back to the last statements。
	 */
	public void rollback() {
		try {
			writer.rollback();
		} catch (IOException e) {
			logger.error("rollback failed: " + e.getMessage());
		}
	}

	/**
	 * Submit a transaction
	 */
	public void submit() {
		try {
			writer.commit();
		} catch (IOException e) {
			logger.error("submit failed: " + e.getMessage());
			rollback();

		}
	}

	/**
	 * destroy the IndexWriter object.
	 */
	public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				logger.error("failed close: " + e.getMessage());
			}
		}

	}

}
