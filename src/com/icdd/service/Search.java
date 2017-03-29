package com.icdd.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;

import com.icdd.lucence.SearchFiles;
import com.icdd.xml.ParserXML;

/**
 * Servlet implementation class Search
 * 提供搜索文献的服务，返回与关键字相关的文献内容。
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Search() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter result = response.getWriter();
		String queryString = request.getParameter("keyword");
		
		SearchFiles searchObj = new SearchFiles();
		List<Document> docs = new ArrayList<Document>();
		docs = searchObj.searchFilesWeb(queryString);
		ParserXML px = new ParserXML();
		
		int i = 0;
		for (Document item : docs) {
			i++;
			result.write(item.get("path"));
			px.readDataSimple(new File(item.get("path")));
			if(i > 3)
				break;
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
