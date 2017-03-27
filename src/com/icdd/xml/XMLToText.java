package com.icdd.xml;

import java.io.File;
import java.io.IOException;

public class XMLToText {
	private static final DataPreprocessor dataPre = new DataPreprocessor();
	private File source;
	private File target;
	private String identity = null;

	public XMLToText() {

	}

	public XMLToText(File source, File target) {
		this.source = source;
		this.target = target;
	}

	public XMLToText(File source, File target, String identity) {
		this.source = source;
		this.target = target;
		this.identity = identity;
	}

	public void xmlToTextRecursive() {
		if(!source.exists()){
			return;
		}
		if (source.isDirectory()) {
			String[] fileNames = source.list();
			// enumerate all files in the directory
			for (int i = 0; i < fileNames.length; i++) {
				File f = new File(source.getPath(), fileNames[i]);
				try {
					System.out.println(f.getCanonicalPath());
				} catch (IOException e) {
					System.out.println("not path");
				}
				// if the file is again a directory, call the xmlToText method
				// recursively
				if (f.isDirectory()) {
					this.source = f;
					xmlToTextRecursive();
				} else {
					
					
					if (identity != null && dataPre.isTeleXML(f, identity)) {
						dataPre.purifyXML(f, target);
					}
				}
			}
		} else {
			if (identity != null && dataPre.isTeleXML(source, identity)) {
				dataPre.purifyXML(source, target);
			}
		}
	}
}
