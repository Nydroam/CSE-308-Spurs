package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import model.Algorithm;
import model.Coordinate;
import model.Election.ElectionType;
import model.Geometry;
import model.State;
import util.DBHelper;

/**
 * Servlet implementation class StateServlet
 */
public class StateServlet extends SpursServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		switch (path) {
		case "/create":{
			Geometry g = new Geometry();
			g.add(new Coordinate(5,1));
			State s = new State(5, g);
		
			DBHelper.saveOrUpdate(s);
			
			System.out.println(s.getId());
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
			JsonObject jsonBody = GSON.fromJson(body, JsonObject.class);
			float voteThresh = jsonBody.get("voteThresh").getAsFloat();
			float raceThresh = jsonBody.get("raceThresh").getAsFloat();
			ElectionType electionType = ElectionType.valueOf(jsonBody.get("electionType").getAsString());
			long stateId = jsonBody.get("stateId").getAsLong();
			
			State state = (State)DBHelper.getObject(State.class, stateId);
			Algorithm a = new Algorithm(state);
			
			sendResponse(res, GSON.toJson(a.runPhase0(electionType, voteThresh, raceThresh)));
			break;
		}
		default:
			break;
		}
	}
}
