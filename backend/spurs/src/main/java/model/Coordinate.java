package model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Coordinate {
	
	private CoordinateKey coordinateKey;
	private GeoEntity geoEntity;
	
	public Coordinate() {
	}

	public Coordinate(double x, double y, GeoEntity geoEntity){
		coordinateKey = new CoordinateKey(x,y,geoEntity.getId());
	}
	
	@Id
	public CoordinateKey getCoordinateKey() {
		return coordinateKey;
	}
	
	public void setCoordinateKey(CoordinateKey coordinateKey) {
		this.coordinateKey = coordinateKey;
	}
	
	@Transient
	public double getX() {
		return coordinateKey.getX();
	}

	public void setX(double x) {
		coordinateKey.setX(x);
	}
	
	@Transient
	public double getY() {
		return coordinateKey.getY();
	}

	public void setY(double y) {
		coordinateKey.setY(y);
	}
	
	@ManyToOne
	@JoinColumn(name="geoEntityId", insertable=false, updatable=false)
	public GeoEntity getGeoEntity() {
		return geoEntity;
	}
	
	public void setGeoEntity(GeoEntity geoEntity) {
		this.geoEntity = geoEntity;
	}
}
