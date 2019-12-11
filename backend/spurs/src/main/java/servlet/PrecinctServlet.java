package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.Coordinate;
import model.Demographic;
import model.District;
import model.Election;
import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;
import model.Precinct;
import model.PrecinctEdge;
import model.Segment;
import model.State;
import model.Votes;
import util.DBHelper;

public class PrecinctServlet extends SpursServlet {

	private static final String MULTIPOLYGON = "MultiPolygon";
	private static final String POLYGON = "Polygon";
	
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
	
			for (JsonElement feature: features) {
				
				//JsonObject geojson = feature.getAsJsonObject().get("geometry").getAsJsonObject();
				JsonObject properties = feature.getAsJsonObject().get("properties").getAsJsonObject();
			
				long districtId = properties.get("district_geoid").getAsLong();
				Precinct precinct = new Precinct(properties.get("id").getAsLong(), (District)DBHelper.getObject(District.class, districtId));
				
				DBHelper.saveOrUpdate(precinct);
				setPrecinctProperties(precinct, properties);
				//setPrecinctCoordinates(precinct, geojson);
				System.out.println("Precinct " + precinct.getId() + " created.");
			}
			System.out.println("Precincts upload done");
			break;
		}
		case "/batchCreateNeighbors": {
			JsonObject jsonBody = GSON.fromJson(body, JsonObject.class);
			for (String precinctIdString: jsonBody.keySet()) {
				long precinctId = Long.parseLong(precinctIdString);
				Precinct precinct1 = (Precinct)DBHelper.getObject(Precinct.class, precinctId);
				List<PrecinctEdge> adjacentEdges = new ArrayList<PrecinctEdge>();
				for (JsonElement neighborJson: jsonBody.get(precinctIdString).getAsJsonArray()) {
					long neighborId = neighborJson.getAsJsonArray().get(0).getAsLong();
					if (neighborId == precinctId) {
						continue;
					}
					Precinct precinct2 = (Precinct)DBHelper.getObject(Precinct.class, neighborId);
					PrecinctEdge edge = new PrecinctEdge(precinct1, precinct2, 0, 0, neighborJson.getAsJsonArray().get(1).getAsFloat());
					adjacentEdges.add(edge);
					//DBHelper.saveOrUpdate(edge);
				}
				precinct1.setAdjacentEdges(adjacentEdges);
				DBHelper.saveOrUpdate(precinct1);
				System.out.println("Precinct " + precinctId + " neighbors saved.");
			}
			System.out.println("Neighbor uploads done");
		}
		}

	}

	private void setPrecinctCoordinates(Precinct precinct, JsonObject geojson) {
		String geojsonType = geojson.get("type").getAsString();
		JsonArray coordinates = geojson.get("coordinates").getAsJsonArray().get(0).getAsJsonArray();
		if (geojsonType.equals(MULTIPOLYGON)) {
			for (JsonElement polygon: coordinates) {
				JsonArray points = polygon.getAsJsonArray();
				for (JsonElement point: points) {
					DBHelper.saveOrUpdate(new Coordinate(point.getAsJsonArray().get(0).getAsDouble(), point.getAsJsonArray().get(1).getAsDouble(), precinct));
				}
			}
		}
		else if (geojsonType.equals(POLYGON)) {
			JsonArray points = coordinates.getAsJsonArray();
			for (JsonElement point: points) {
				DBHelper.saveOrUpdate(new Coordinate(point.getAsJsonArray().get(0).getAsDouble(), point.getAsJsonArray().get(1).getAsDouble(), precinct));
			}
		}
		
	}

	private void setPrecinctProperties(Precinct precinct, JsonObject properties) {
		precinct.setName(properties.get("NAME").getAsString());
		precinct.setCounty(properties.get("COUNTY").getAsString());
		precinct.setArea(properties.get("area").getAsFloat());
		precinct.setPerimeter(properties.get("perimeter").getAsFloat());
		
		int GOV18R = properties.get("GOV18R").getAsInt();
		int GOV18D = properties.get("GOV18D").getAsInt();
		int SEN18R = properties.get("SEN18R").getAsInt();
		int SEN18D = properties.get("SEN18D").getAsInt();
		int PRES16R = properties.get("PRES16R").getAsInt();
		int PRES16D = properties.get("PRES16D").getAsInt();
		
		Party GOV18win = GOV18R > GOV18D? Party.REPUBLICAN: Party.DEMOCRAT; 
		Party SEN18win = SEN18R > SEN18D? Party.REPUBLICAN: Party.DEMOCRAT;
		Party PRES16win = PRES16R > PRES16D? Party.REPUBLICAN: Party.DEMOCRAT;
		
		Election electionGOV18 = new Election(precinct, ElectionType.GOV18, GOV18win);
		Election electionSEN18 = new Election(precinct, ElectionType.SEN18, SEN18win);
		Election electionPRES16 = new Election(precinct, ElectionType.PRES16, PRES16win);
		
		DBHelper.saveOrUpdate(electionGOV18);
		DBHelper.saveOrUpdate(electionSEN18);
		DBHelper.saveOrUpdate(electionPRES16);
		
		Votes GOV18Rvote = new Votes(electionGOV18, Party.REPUBLICAN, GOV18R);
		Votes GOV18Dvote = new Votes(electionGOV18, Party.DEMOCRAT, GOV18D);
		Votes SEN18Rvote = new Votes(electionSEN18, Party.REPUBLICAN, SEN18R);
		Votes SEN18Dvote = new Votes(electionSEN18, Party.DEMOCRAT, SEN18D);
		Votes PRES16Rvote = new Votes(electionPRES16, Party.REPUBLICAN, PRES16R);
		Votes PRES16Dvote = new Votes(electionPRES16, Party.DEMOCRAT, PRES16D);
		
		DBHelper.saveOrUpdate(GOV18Rvote);
		DBHelper.saveOrUpdate(GOV18Dvote);
		DBHelper.saveOrUpdate(SEN18Rvote);
		DBHelper.saveOrUpdate(SEN18Dvote);
		DBHelper.saveOrUpdate(PRES16Rvote);
		DBHelper.saveOrUpdate(PRES16Dvote);
		
		long totalPopulation = 0;
		for (Race race: Race.values()) {
			long population = properties.get(race.toString()).getAsLong();
			totalPopulation += population;
			DBHelper.saveOrUpdate(new Demographic(race, precinct, population));
		}
		precinct.setPopulation(totalPopulation);
		
		DBHelper.saveOrUpdate(precinct);
	}

}
