package com.icdd.xml;

import java.io.File;
import java.io.IOException;

public class XMLHandler {
	private static final DataPreprocessor dataPre = new DataPreprocessor();
	private File source;
	private File target;
	private static final int MAX_XML_FILES = 50000; 
	private int num = 0;

	public XMLHandler() {
		//
	}

	public XMLHandler(File source, File target) {
		this.source = source;
		this.target = target;
	}

	public void xmlsFormat() {
		if (source.isDirectory()) {
			String[] fileNames = source.list();
			// enumerate all files in the directory
			for (int i = 0; i < fileNames.length; i++) {
				File f = new File(source.getPath(), fileNames[i]);
				try {
					System.out.println(f.getCanonicalPath());
				} catch (IOException e) {
					System.out.println("this file not exits");
}
				// if the file is again a directory, call the xmlToText method
				// recursively
				if (f.isDirectory()) {
					this.source = f;
					xmlsFormat();
				} else {
					xmlFormat(f);
				}
			}
		} else {
			xmlFormat(source);
		}
	}

	public void xmlFormat(File file) {
		num++;
		if(num > MAX_XML_FILES){
			System.exit(0);
		}
		if (dataPre.isTeleXML(file)) {
			dataPre.purifyXML(file, target);
		}
	}
}
