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
package com.icdd.lucence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class SearchFiles {
	public SearchFiles() {
		//
	}

	public void searchFiles() {
		// 索引目录（文件）
		String index = "F:/download/index/";
		// 索引字段
		String field = "contents";
		// 查询文件
		String queries = null;
		// 查询字符串
		String queryString = null;
		int repeat = 0;
		// 是否打印索引文件的源文件信息
		boolean raw = false;
		// 每页显示10条结果
		int hitsPerPage = 10;

		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();

			BufferedReader in = null;
			if (queries != null) {
				in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
			} else {
				in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
			}
			QueryParser parser = new QueryParser(field, analyzer);
			while (true) {
				if (queryString == null && queries == null) {
					System.out.println("Enter query:");
				}

				String line = queryString != null ? queryString : in.readLine();

				if (line == null || line.length() == -1) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					break;
				}
				try {
					Query query = parser.parse(line);
					System.out.println("Searching for:" + query.toString(field));

					if (repeat > 0) { // repeat & time as benchmark
						Date start = new Date();
						for (int i = 0; i < repeat; i++) {
							searcher.search(query, 100);
						}
						Date end = new Date();
						System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");
					}

					doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);

				} catch (ParseException e) {

					e.printStackTrace();
				}

				if (queryString != null) {
					break;
				}
			}
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage,
			boolean raw, boolean interactive) throws IOException {
		// Collect enough docs to show 5 pages
		Date starttime = new Date();

		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + "total matching documents");
		Date endtime = new Date();
		System.out.println("one search cost :" + (endtime.getTime() - starttime.getTime()) + "ms");
		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			if (end > hits.length) {
				System.out.println("Only results 1 - " + hits.length + " of " + numTotalHits
						+ " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}
				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) {
					System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				if (path != null) {
					System.out.println((i + 1) + ". " + path);
					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title:" + title);
					}
				} else {
					System.out.println((i + 1) + ". " + "No path for this document");
				}
			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press");
					if (start - hitsPerPage > 0) {
						System.out.print("(p)revious page,");
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page,");
					}
					System.out.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0) == 'q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start += hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page.");
						}
					}
				}
				if (quit)
					break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}

	}

	public List<Document> searchFilesWeb(String queryString) {
		// 索引目录（文件）
		String index = "F:/download/index/";
		// 索引字段
		String field = "contents";
		int repeat = 0;
		// 是否打印索引文件的源文件信息
		boolean raw = false;
		// 每页显示10条结果
		int hitsPerPage = 10;
		List<Document> docs = new ArrayList<Document>();
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();

			QueryParser parser = new QueryParser(field, analyzer);
			
			if (queryString == null) {
				System.out.println("Enter query:");
				return null;
			}
			String line = queryString;
			line = line.trim();
			if (line.length() == 0) {
				return null;
			}
			try {
				Query query = parser.parse(line);
				System.out.println("Searching for:" + query.toString(field));

				if (repeat > 0) { // repeat & time as benchmark
					Date start = new Date();
					for (int i = 0; i < repeat; i++) {
						searcher.search(query, 100);
					}
					Date end = new Date();
					System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");
				}

				docs = doPagingSearchWeb(docs, searcher, query, hitsPerPage, raw);
			} catch (ParseException e) {

				e.printStackTrace();
			}
			reader.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
		return docs;
	}

	public static List<Document> doPagingSearchWeb(List<Document> docs, IndexSearcher searcher, Query query, int hitsPerPage,
			boolean raw) throws IOException {
		// Collect enough docs to show 5 pages
		Date starttime = new Date();

		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + "total matching documents");
		Date endtime = new Date();
		System.out.println("one search cost :" + (endtime.getTime() - starttime.getTime()) + "ms");		
		
		System.out.println("搜索结果列表：");
		
		for (ScoreDoc scoreDoc : hits) {
			docs.add(searcher.doc(scoreDoc.doc));
		}
		return docs;
	}
}
