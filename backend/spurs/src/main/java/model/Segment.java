package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

public class Segment {
	
	private Set<Coordinate> coordinates;
	private GeoEntity geoEntity;
	private long geoEntityId;
	private String id;
	
	public Segment() {
	}

	public Segment(GeoEntity geoEntity) {
		this.geoEntity = geoEntity;
		this.geoEntityId = geoEntity.getId();
	}
	
	public Segment(double x1, double y1, double x2, double y2, GeoEntity geoEntity) {
		this(geoEntity);
		id = buildId(x1, y1, x2, y2);	
	}
	
	public Segment(Coordinate coor1, Coordinate coor2, GeoEntity geoEntity) {
		this(geoEntity);
		coordinates.add(coor1);
		coordinates.add(coor2);
		double x1 = coor1.getX();
		double y1 = coor1.getY();
		double x2 = coor2.getX();
		double y2 = coor2.getY();
		id = buildId(x1, y1, x2, y2);
	}
	
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@OneToMany(targetEntity=Coordinate.class, mappedBy="segment", cascade=CascadeType.ALL)
	public Set<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Set<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	@ManyToOne
	@JoinColumn(name="geoEntityId", insertable=false, updatable=false)
	public GeoEntity getGeoEntity() {
		return geoEntity;
	}

	public void setGeoEntity(GeoEntity geoEntity) {
		this.geoEntity = geoEntity;
	}

	public long getGeoEntityId() {
		return geoEntityId;
	}

	public void setGeoEntityId(long geoEntityId) {
		this.geoEntityId = geoEntityId;
	}
	
	public String buildId(double x1, double x2, double y1, double y2) {
		return x1 + "," + y1 + "_" + x2 + "," + y2 + ":" + geoEntityId;
	}
	
}
