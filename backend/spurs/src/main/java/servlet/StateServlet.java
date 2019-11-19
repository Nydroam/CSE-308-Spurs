package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import model.Algorithm;
import model.Coordinate;
import model.Election.ElectionType;
import model.State;
import util.DBHelper;

/**
 * Servlet implementation class MapServlet
 */
public class StateServlet extends SpursServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		switch (path) {
		case "/create":{
			break;
		}
		case "/get":{
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void post(HttpServletRequest req, HttpServletResponse res, String body)
			throws ServletException, IOException {
		String path = req.getPathInfo();
		switch (path) {
		case "/runPhase0":{
			JsonObject jsonBody = new JsonObject();
			float voteThresh = jsonBody.get("voteThresh").getAsFloat();
			float raceThresh = jsonBody.get("raceThresh").getAsFloat();
			ElectionType electionType = ElectionType.valueOf(jsonBody.get("electionType").getAsString());
			long stateId = jsonBody.get("stateId").getAsLong();
			State state = (State)DBHelper.getObject(State.class, stateId);
			
			Algorithm a = new Algorithm();
			
			sendResponse(res, GSON.toJson(a.runPhase0(state, electionType, voteThresh, raceThresh)));
			break;

		}
		default:
			break;
		}
	}
}
