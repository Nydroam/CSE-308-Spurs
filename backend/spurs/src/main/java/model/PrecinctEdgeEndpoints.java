package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Embeddable
public class PrecinctEdgeEndpoints implements Serializable{
	
	private Precinct endpoint1;
	private Precinct endpoint2;
	
	public PrecinctEdgeEndpoints() {
	}
	
	public PrecinctEdgeEndpoints(Precinct endpoint1, Precinct endpoint2) {
		this.setEndpoint1(endpoint1);
		this.setEndpoint2(endpoint2);
	}

	@ManyToOne
	public Precinct getEndpoint1() {
		return endpoint1;
	}

	public void setEndpoint1(Precinct endpoint1) {
		this.endpoint1 = endpoint1;
	}
	
	@ManyToOne
	public Precinct getEndpoint2() {
		return endpoint2;
	}

	public void setEndpoint2(Precinct endpoint2) {
		this.endpoint2 = endpoint2;
	}
	
}
