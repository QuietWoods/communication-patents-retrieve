package com.icdd.lucene;

import java.io.File;
import java.io.FileFilter;
/**
 * Filter the file by it's suffix name, build the index.
 * @author wl
 *
 */
public class xmlFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".xml");
	}

}
