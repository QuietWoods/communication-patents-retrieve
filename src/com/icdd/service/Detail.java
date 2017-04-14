package com.icdd.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import com.icdd.xml.ParserSource;

/**
 * Servlet implementation class Detail
 */
@WebServlet("/Detail")
public class Detail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final Logger logger = (Logger) LogManager.getLogger("mylog");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Detail() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.error("Served at: "+request.getContextPath().getClass());
		long start = System.currentTimeMillis();
		HttpSession session = request.getSession(true);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter result = response.getWriter();
		String path = request.getParameter("path");
		if(path == null){
			result.println("path: "+path);
			return;
		}
		
		ParserSource px = new ParserSource();
		File file = new File(path);

		Map<String, String> detail  = px.readData(file);
		/*for (Map.Entry<String, String> entry : detail.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + ": " + value);
		}*/
		session.setAttribute("detail", detail);		
		response.sendRedirect("detail.jsp");
		long end = System.currentTimeMillis();
		logger.info("Detail.Servlet:"+(end-start)+" milliseconds");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
