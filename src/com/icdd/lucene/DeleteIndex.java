package com.icdd.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DeleteIndex extends IndexLucene{
	private static IndexWriter writer = null;

	public DeleteIndex(boolean mode) {
		boolean create = mode;
		
		try {
			Directory dir = FSDirectory.open(Paths.get(INDEX_PATH));
			IndexWriterConfig iwc = new IndexWriterConfig(ANALYZER);
			if(create)
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			else{
				iwc.setOpenMode(OpenMode.CREATE);
			}
			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			e.printStackTrace();
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
}
