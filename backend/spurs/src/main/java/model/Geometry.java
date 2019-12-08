package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public class Geometry {
	
	private GeoEntity geoEntity;
	private List<Coordinate> coordinates;
	
	public Geometry() {
	}
	
	public Geometry(GeoEntity geoEntity) {
		this.geoEntity = geoEntity;
	}
	
	@Id	
	@OneToOne
	public GeoEntity getGeoEntity() {
		return geoEntity;
	}

	public void setGeoEntity(GeoEntity geoEntity) {
		this.geoEntity = geoEntity;
	}

	@OneToMany
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}
	
	public void add(Coordinate coordinate) {
		if (coordinates == null) {
			coordinates = new ArrayList<Coordinate>();
		}
		coordinates.add(coordinate);
	}
}
