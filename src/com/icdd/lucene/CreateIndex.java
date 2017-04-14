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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CreateIndex extends IndexLucene {
	private static IndexWriter writer = null;
	private static final int offset = 0;     //索引50000份专利文献
	private static final int endset = 50000;
	private static int num = 0;
    
    /*
     * mode标识创建索引的模式，true：OpenMode.CREATE;  false:OpenMode.CREATE_OR_APPEND.
     */
	public CreateIndex(boolean mode) {
		boolean create = mode;
		try {
			Directory dir = FSDirectory.open(Paths.get(INDEX_PATH));
			IndexWriterConfig iwc = new IndexWriterConfig(ANALYZER);
			if (create)
				iwc.setOpenMode(OpenMode.CREATE);
			else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * create or update index files
	 */
	public void createOrUpdateIndex(String docsPath) {
		Path docDir = Paths.get(docsPath);
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
			writer.close();
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
			
			System.out.println("num: "+num);
			num++;
			if (num < endset && num >= offset) {
				
				try (InputStream stream = Files.newInputStream(file)) {
					// make a new,empty document
					Document doc = new Document();

					Field pathField = new StringField("path", file.toString(), Field.Store.YES);
					String filename = file.getFileName().toString();
					int post = filename.indexOf('_');
					if(post > 0){
						filename = filename.substring(post+1, filename.length()-4);
					}
				
					doc.add(pathField);
					doc.add(new StringField("title", filename, Field.Store.YES));
					doc.add(new SortedNumericDocValuesField("modified", lastModified));
					doc.add(new TextField("contents",
							new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// New index, so we just add the document (no old
						// document
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
	}
}
