package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.Algorithm;
import model.Election.ElectionType;
import model.Election.Race;
import model.PrecinctCluster;
import model.PrecinctClusterEdge;
import model.State;
import model.State.StateName;
import util.DBHelper;

/**
 * Servlet implementation class StateServlet
 */
public class StateServlet extends SpursServlet{
	private static final long serialVersionUID = 1L;

	private Map<Long, State> states = new HashMap<Long, State>();
	private Map<Long, Algorithm> algo = new HashMap<Long, Algorithm>();
	
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
			State state = null;
			if (states.containsKey(stateId)) {
				state = states.get(stateId);
			}else {
				state = (State)DBHelper.getObject(State.class, stateId);
				states.put(stateId, state);
			}
			Algorithm a = null;
			if(algo.containsKey(stateId)) {
				a = algo.get(stateId);
			}else {
				a = new Algorithm(state);
				algo.put(stateId, a);
			}
			sendResponse(res, GSON.toJson(a.runPhase0(electionType, voteThresh, raceThresh)));
			break;
		}
		case "/runPhase1":{
			JsonObject jsonBody = GSON.fromJson(body, JsonObject.class);
			float rangeMin = jsonBody.get("rangeMin").getAsFloat()/100;
			float rangeMax = jsonBody.get("rangeMax").getAsFloat()/100;
			boolean step = jsonBody.get("step").getAsBoolean();
			int distNum = jsonBody.get("distNum").getAsInt();
			JsonObject w = jsonBody.get("weights").getAsJsonObject();
			PrecinctClusterEdge.COMPACTNESS_WEIGHT = w.get("compactnessWeight").getAsFloat();
			PrecinctClusterEdge.COUNTY_WEIGHT = w.get("countyWeight").getAsFloat();
			PrecinctClusterEdge.FAIRNESS_WEIGHT = w.get("fairnessWeight").getAsFloat();
			PrecinctClusterEdge.POPULATION_WEIGHT = w.get("populationWeight").getAsFloat();
			PrecinctClusterEdge.MM_WEIGHT = w.get("mmWeight").getAsFloat();
			
			ArrayList<Race> races = new ArrayList<Race>();
			for (JsonElement element: jsonBody.get("races").getAsJsonArray()) {
				races.add(Race.valueOf(element.getAsString()));
			}
			long stateId = jsonBody.get("stateId").getAsLong();
			State state = null;
			System.out.println(states.keySet().size());
			System.out.println(states.containsKey(stateId));
			if (states.containsKey(stateId)) {
				state = states.get(stateId);
			}else {
				state = (State)DBHelper.getObject(State.class, stateId);
				states.put(stateId, state);
			}
			Algorithm a = null;
			if(algo.containsKey(stateId) && step) {
				System.out.println("HERE");
				a = algo.get(stateId);
			}else {
				a = new Algorithm(state);
				algo.put(stateId, a);
			}
			
			if(!step) {
				a.resetClusters();
				sendResponse(res, GSON.toJson(a.runPhase1(races, rangeMin, rangeMax, distNum)));
				a.resetClusters();
				algo.remove(stateId);
			}
			else {
				HashMap<String,Object> map = new HashMap<String,Object>();
				Set<PrecinctCluster> curr = a.runPhase1Step(races, rangeMin, rangeMax, distNum);
				for(int i = 1 ; i < jsonBody.get("numSteps").getAsInt() ; i++) {
					if (a.finishedPhase1())
						break;
					curr = a.runPhase1Step(races, rangeMin, rangeMax, distNum);
				}
				map.put("results",curr);
				map.put("finished", a.finishedPhase1());
				sendResponse(res, GSON.toJson(map));
				if(a.finishedPhase1()) {
					a.resetClusters();
					algo.remove(stateId);
				}
			}
			break;
		}
		default:
			break;
		}
	}
}
