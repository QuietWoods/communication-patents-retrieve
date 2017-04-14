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

import java.io.FileFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public abstract class IndexLucene {	
	public static final Logger logger = (Logger) LogManager.getLogger("mylog");
	protected static final FileFilter filter = new xmlFileFilter();
	protected static final String INDEX_PATH = "f:\\download\\index";
	protected static final String DOCS_PATH = "f:\\download\\source";
	
	protected static final Analyzer ANALYZER = new SmartChineseAnalyzer();
	protected static final Analyzer SDANALYZER = new StandardAnalyzer();
}
