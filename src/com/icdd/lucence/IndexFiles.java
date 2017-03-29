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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

public class IndexFiles {
	private static Logger logger = (Logger) LogManager.getLogger("mylog");
	private String indexPath;
	private String docsPath;

	public IndexFiles(String indexPath, String docsPath) {
		this.indexPath = indexPath;
		this.docsPath = docsPath;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public String getDocsPath() {
		return docsPath;
	}

	public void index() {
		//默认是OpenMode.CREATE
		index(true);
	}

	public void index(boolean mode) {
		boolean create = mode;
		final Path docDir = Paths.get(docsPath);
		if (!Files.isReadable(docDir)) {
			logger.warn("Document directory '" + docDir + "'does not exist or  is not readable, "
					+ "please check the path");
			System.exit(1);
		}
		Date start = new Date();
		try {
			logger.warn("Indexing to directory '" + indexPath + "'...");
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			writer.close();
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
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

					}
					return FileVisitResult.CONTINUE;
				}
			});

		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			// make a new,empty document
			Document doc = new Document();

			Field pathField = new StringField("path", file.toString(), Field.Store.YES);

			doc.add(pathField);
			doc.add(new SortedNumericDocValuesField("modified", lastModified));
			doc.add(new TextField("contents",
					new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can
				// be there):
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been
				// indexed) so
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}

	@Override
	public String toString() {
		return "IndexFiles [indexPath=" + indexPath + ", docsPath=" + docsPath + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((docsPath == null) ? 0 : docsPath.hashCode());
		result = prime * result + ((indexPath == null) ? 0 : indexPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexFiles other = (IndexFiles) obj;
		if (docsPath == null) {
			if (other.docsPath != null)
				return false;
		} else if (!docsPath.equals(other.docsPath))
			return false;
		if (indexPath == null) {
			if (other.indexPath != null)
				return false;
		} else if (!indexPath.equals(other.indexPath))
			return false;
		return true;
	}

}
