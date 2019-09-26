package servlet;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public abstract class SpursServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected static Gson GSON = new Gson();
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("GET " + request.getServletPath() + request.getPathInfo());
		get(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("POST " + request.getServletPath() + request.getPathInfo());
		post(request, response, request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
	}

	protected abstract void get(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;
	
	protected abstract void post(HttpServletRequest req, HttpServletResponse res, String body) throws ServletException, IOException;
	
}
