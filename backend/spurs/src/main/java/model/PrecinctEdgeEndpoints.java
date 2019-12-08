package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Embeddable
public class PrecinctEdgeEndpoints implements Serializable{
	
	private long endpoint1Id;
	private long endpoint2Id;
	
	public PrecinctEdgeEndpoints() {
	}
	
	public PrecinctEdgeEndpoints(Precinct endpoint1, Precinct endpoint2) {
		this.setEndpoint1(endpoint1.getId());
		this.setEndpoint2(endpoint2.getId());
	}

	public long getEndpoint1() {
		return endpoint1Id;
	}

	public void setEndpoint1(long l) {
		this.endpoint1Id = l;
	}
	
	public long getEndpoint2() {
		return endpoint2Id;
	}

	public void setEndpoint2(long l) {
		this.endpoint2Id = l;
	}
	
}
