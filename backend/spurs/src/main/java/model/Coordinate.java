package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Coordinate {
	
	private double x;
	private double y;
	@Id
	private String coorKey;
	@ManyToOne
	private Geometry geometry;
	
	public Coordinate() {
	}

	public Coordinate(double x, double y){
		this.x = x;
		this.y = y;
		coorKey = x + "," + y;
	}
	
	public String getCoorKey() {
		return coorKey;
	}
	
	public void setCoorKey(String coorKey) {
		this.coorKey = coorKey;
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
