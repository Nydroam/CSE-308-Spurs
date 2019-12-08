package servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Coordinate;
import model.Demographic;
import model.District;
import model.PrecinctEdge;
import model.Segment;
import model.Election;
import model.GeoEntity;
import model.Geometry;
import model.Precinct;
import model.State;
import model.Votes;
import util.DBHelper;

public abstract class SpursServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected static Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	
	public static SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml")
			.addAnnotatedClass(State.class)
			.addAnnotatedClass(Coordinate.class)
			.addAnnotatedClass(Precinct.class)
			.addAnnotatedClass(District.class)
			.addAnnotatedClass(PrecinctEdge.class)
			.addAnnotatedClass(Election.class)
			.addAnnotatedClass(Demographic.class)
			.addAnnotatedClass(Votes.class)
			.buildSessionFactory();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("GET " + request.getServletPath() + request.getPathInfo());
		get(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("POST " + request.getServletPath() + request.getPathInfo());
		post(request, response, request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
	}

	protected abstract void get(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;
	
	protected abstract void post(HttpServletRequest req, HttpServletResponse res, String body) throws ServletException, IOException;
	
	protected void sendResponse(HttpServletResponse res, String response) throws IOException{
		res.getWriter().print(response);
	}
	
}
