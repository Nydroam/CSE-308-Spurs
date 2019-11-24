package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Geometry {
	
	private long id;
	private GeoEntity geoEntity;
	private List<Coordinate> coordinates;
	
	public Geometry() {
	}
	
	public Geometry(long id) {
		this.id = id;
	}
	
	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
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
