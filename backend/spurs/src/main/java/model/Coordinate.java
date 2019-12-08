package model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

@Entity
public class Coordinate {
	@Expose
	private CoordinateKey coordinateKey;
	private Precinct precinct;
	
	public Coordinate() {
	}

	public Coordinate(double x, double y, long precinctId) {
		coordinateKey = new CoordinateKey(x,y,precinctId);
	}
	
	public Coordinate(double x, double y, Precinct precinct){
		coordinateKey = new CoordinateKey(x,y,precinct.getId());
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
	@JoinColumn(name="precinctId", insertable=false, updatable=false)
	public Precinct getPrecinct() {
		return precinct;
	}
	
	public void setPrecinct(Precinct precinct) {
		this.precinct = precinct;
	}
}
