package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.google.gson.annotations.Expose;

import model.Election.Race;

@Embeddable
public class DemographicKey implements Serializable{
	@Expose
	private Race race;
	private long precinctId;
	
	public DemographicKey() {	
	}

	public DemographicKey(Race race, long precinctId) {
		this.race = race;
		this.setPrecinctId(precinctId);
	}
	
	@Enumerated(EnumType.STRING)
	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public long getPrecinctId() {
		return precinctId;
	}

	public void setPrecinctId(long precinctId) {
		this.precinctId = precinctId;
	}

}
