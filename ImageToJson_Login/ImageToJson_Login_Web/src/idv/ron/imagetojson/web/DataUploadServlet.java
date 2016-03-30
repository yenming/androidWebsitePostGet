package idv.ron.imagetojson.web;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;

import java.io.*;

@SuppressWarnings("serial")
@WebServlet("/DataUploadServlet")
public class DataUploadServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";
	private ServletContext context;

	@Override
	public void init() throws ServletException {
		context = getServletContext();
	}

	@Override
	public void doPost(HttpServletRequest rq, HttpServletResponse rp)
			throws ServletException, IOException {
		Gson gson = new Gson();
		String jsonStr = rq.getParameter("jsonStr");
		User user = gson.fromJson(jsonStr, User.class);
		context.setAttribute("image", user.getImage());
		String outStr = gson.toJson(user);

		rp.setContentType(CONTENT_TYPE);
		PrintWriter out = rp.getWriter();
		out.println(outStr);
	}

	@Override
	public void doGet(HttpServletRequest rq, HttpServletResponse rp)
			throws ServletException, IOException {
		byte[] image = (byte[]) context.getAttribute("image");
		if (image == null) {
			rp.setContentType(CONTENT_TYPE);
			PrintWriter out = rp.getWriter();
			out.println("No data!");
			out.close();
			return;
		}
		rp.setContentType("image/jpeg");
		rp.setContentLength(image.length);
		ServletOutputStream out = rp.getOutputStream();
		out.write(image);
	}

}