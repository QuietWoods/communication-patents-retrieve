package com.icdd.lucene;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * 给字符串中非汉字子串加引号
 */
public class QueryToCategory {
	private static final String REGEX = "[^\u4e00-\u9fa5]+";	
	private static String  input; 
	private String result;
	public QueryToCategory(String query){
		this.result = getFormatQuery(query);
	}

	public String getResult() {
		return result;
	}

	static List<String> getNonHanZi(){	
	   List<String> content = new ArrayList<>();
		
		Pattern p = Pattern.compile(REGEX);
        String u = input;
        Matcher m = p.matcher(u);        
        while (m.find()) {
            content.add(m.group());  
        }
       return content;
	}
	static String getFormatQuery(String query) {
		input = query;
		List<String> content = getNonHanZi();
		String results = query;
		for (String item : content) {
			results = results.replace(item, "\""+item+"\"");
		}		
		return results;
	}

	
}
