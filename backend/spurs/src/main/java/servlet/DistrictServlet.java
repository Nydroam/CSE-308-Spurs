package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.Algorithm;
import model.Coordinate;
import model.District;
import model.State;
import model.Election.ElectionType;
import model.Segment;
import util.DBHelper;

public class DistrictServlet extends SpursServlet {

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		switch (path) {
		case "/get": {
			break;
		}
		}

	}

	@Override
	protected void post(HttpServletRequest req, HttpServletResponse res, String body)
			throws ServletException, IOException {
		String path = req.getPathInfo();
		switch (path) {
		case "/batchCreate": {
			JsonObject jsonBody = GSON.fromJson(body, JsonObject.class);
			JsonArray features = jsonBody.get("features").getAsJsonArray();
			State state = (State)DBHelper.getObject(State.class, features.get(0).getAsJsonObject().get("properties").getAsJsonObject().get("state_id").getAsLong());
			Set<District> districts = new HashSet<District>();
			for (JsonElement feature: features) {
				
				District district = new District();
				district.setId(feature.getAsJsonObject().get("properties").getAsJsonObject().get("geoid").getAsLong());
				district.setState(state);
				DBHelper.saveOrUpdate(district);				
			}
			break;
		}
		}
	}

}
