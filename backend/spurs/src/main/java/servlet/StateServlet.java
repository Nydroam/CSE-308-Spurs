package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.Algorithm;
import model.Coordinate;
import model.District;
import model.Election.ElectionType;
import model.Election.Race;
import model.Geometry;
import model.Precinct;
import model.State;
import model.State.StateName;
import util.DBHelper;

/**
 * Servlet implementation class StateServlet
 */
public class StateServlet extends SpursServlet{
	private static final long serialVersionUID = 1L;

	public StateServlet() {
	}
	
	@Override
	protected void get(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		switch (path) {
		case "/create":{
			String stateName = req.getParameter("state");
			long stateId = Long.parseLong(req.getParameter("stateId"));
			State state = new State(stateId, StateName.valueOf(stateName));
			DBHelper.saveOrUpdate(state);
			break;
		}
		case "/get":{
			
			break;
		}
		case "/test":{
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
			long ini = System.currentTimeMillis();
			State state = (State)DBHelper.getObject(State.class, stateId);
			System.out.println((System.currentTimeMillis() - ini)/1000.0);
			ini = System.currentTimeMillis();
			Algorithm a = new Algorithm(state);
			System.out.println((System.currentTimeMillis() - ini)/1000.0);
			sendResponse(res, GSON.toJson(a.runPhase0(electionType, voteThresh, raceThresh)));
			break;
		}
		case "/runPhase1":{
			JsonObject jsonBody = GSON.fromJson(body, JsonObject.class);
			float rangeMin = jsonBody.get("rangeMin").getAsFloat();
			float rangeMax = jsonBody.get("rangeMax").getAsFloat();
			ArrayList<Race> races = new ArrayList<Race>();
			for (JsonElement element: jsonBody.get("races").getAsJsonArray()) {
				races.add(Race.valueOf(element.getAsString()));
			}
			long stateId = jsonBody.get("stateId").getAsLong();
			
			State state = (State)DBHelper.getObject(State.class, stateId);
			Algorithm a = new Algorithm(state);
			
			sendResponse(res, GSON.toJson(a.runPhase1(races, rangeMin, rangeMax)));
			break;
		}
		default:
			break;
		}
	}
}
