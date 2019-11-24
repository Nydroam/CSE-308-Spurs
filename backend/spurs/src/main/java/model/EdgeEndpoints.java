package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Embeddable
public class EdgeEndpoints implements Serializable{
	
	@ManyToOne
	private GeoEntity endpoint1;
	@ManyToOne
	private GeoEntity endpoint2;
	
	public EdgeEndpoints() {
	}
	
	public EdgeEndpoints(GeoEntity endpoint1, GeoEntity endpoint2) {
		this.setEndpoint1(endpoint1);
		this.setEndpoint2(endpoint2);
	}

	public GeoEntity getEndpoint1() {
		return endpoint1;
	}

	public void setEndpoint1(GeoEntity endpoint1) {
		this.endpoint1 = endpoint1;
	}

	public GeoEntity getEndpoint2() {
		return endpoint2;
	}

	public void setEndpoint2(GeoEntity endpoint2) {
		this.endpoint2 = endpoint2;
	}
	
}
