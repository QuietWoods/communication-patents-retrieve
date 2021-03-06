package com.icdd.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.lucene.document.Document;

import com.icdd.lucene.QueryToCategory;
import com.icdd.lucene.SearchIndex;

/**
 * Servlet implementation class Search
 * 提供搜索文献的服务，返回与关键字相关的文献内容。
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final Logger logger = (Logger) LogManager.getLogger("mylog");
    /**
     * Default constructor. 
     */
    public Search() {
        //
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.error("Served at: "+request.getContextPath());
		long start = System.currentTimeMillis();
		HttpSession session = request.getSession(true);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter result = response.getWriter();
		String queryString = request.getParameter("keyword");
		String category = request.getParameter("Category");
		
		if(category != null){
			//queryString = new  QueryToCategory(category).getResult();
			queryString = category;
		}
		if(queryString == null){
			result.println("keyword: "+queryString);
			return;
		}else{
			queryString = new  QueryToCategory(queryString).getResult();
		}
		SearchIndex index = new SearchIndex();
		
		List<Document> docs = index.searchFiles(queryString, "contents");
		session.setAttribute("docs", docs);
		
		if(category != null){
			session.setAttribute("category", category);
			response.sendRedirect("category.jsp");
		}else{
			response.sendRedirect("index.jsp");
		}
		
		long end = System.currentTimeMillis();
		logger.info("Detail.Servlet:"+(end-start)+" milliseconds");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
