package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.gson.annotations.Expose;

@Embeddable
public class CoordinateKey implements Serializable{
	
	@Expose
	private double x;
	@Expose
	private double y;
	@Expose
	private long precinctId;
	
	public CoordinateKey() {	
	}
	
	public CoordinateKey(double x, double y, long precinctId) {
		this.x = x;
		this.y = y;
		this.precinctId = precinctId;
	}
	
	public long getPrecinctId() {
		return precinctId;
	}
	public void setPrecinctId(long precinctId) {
		this.precinctId = precinctId;
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
