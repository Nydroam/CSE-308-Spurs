package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class CoordinateKey implements Serializable{
	
	private double x;
	private double y;
	private long geoEntityId;
	
	public CoordinateKey() {	
	}
	
	public CoordinateKey(double x, double y, long geoEntityId) {
		this.geoEntityId = geoEntityId;
	}
	
	public long getGeoEntityId() {
		return geoEntityId;
	}
	public void setGeoEntityId(long geoEntityId) {
		this.geoEntityId = geoEntityId;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	
}
