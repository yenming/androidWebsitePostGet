package idv.ron.texttojson.web;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	// private ServletContext context;
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";
	private List<String> category;
	private List<Book> computerList;
	private List<Book> comicList;

	@Override
	public void init() throws ServletException {
		super.init();
		// context = getServletContext();
		category = new ArrayList<String>();
		category.add("all");
		category.add("computer books");
		category.add("comic books");

		// contentType = context.getInitParameter("contentType");
		computerList = new ArrayList<Book>();
		computerList.add(new Book(1, "Java Programming", 500, "John", category
				.get(1)));
		computerList.add(new Book(2, "Android App Development", 600, "Allen",
				category.get(1)));
		comicList = new ArrayList<Book>();
		comicList.add(new Book(3, "One Piece", 120, "尾田榮一郎", category.get(2)));
		comicList.add(new Book(4, "City Hunter", 100, "北條司", category.get(2)));
	}

	@Override
	public void doPost(HttpServletRequest rq, HttpServletResponse rp)
			throws ServletException, IOException {
		Gson gson = new Gson();
		rq.setCharacterEncoding("UTF-8");
		String param = rq.getParameter("param");
		String outStr = "";
		if (param.equals("category")) {
			outStr = gson.toJson(category);
		} else if (param.equals(category.get(0))) {
			List<Book> bookList = new ArrayList<Book>();
			bookList.addAll(computerList);
			bookList.addAll(comicList);
			outStr = gson.toJson(bookList);
		} else if (param.equals(category.get(1))) {
			outStr = gson.toJson(computerList);
		} else if (param.equals(category.get(2))) {
			outStr = gson.toJson(comicList);
		}

		rp.setContentType(CONTENT_TYPE);
		PrintWriter out = rp.getWriter();
		out.println(outStr);
	}

	@Override
	public void doGet(HttpServletRequest rq, HttpServletResponse rp)
			throws ServletException, IOException {

		Gson gson = new Gson();
		String category_json = gson.toJson(category);
		String computerList_json = gson.toJson(computerList);
		String comicList_json = gson.toJson(comicList);
		rp.setContentType(CONTENT_TYPE);
		PrintWriter out = rp.getWriter();
		out.println("<H3>Category</H3>");
		out.println(category_json);
		out.println("<H3>Computer Books</H3>");
		out.println(computerList_json);
		out.println("<H3>Comic Books</H3>");
		out.println(comicList_json);
	}

}