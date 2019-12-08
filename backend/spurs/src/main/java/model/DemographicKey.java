package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import model.Election.Race;

@Embeddable
public class DemographicKey implements Serializable{
	private Race race;
	@ManyToOne
	private Precinct precinct;
	
	public DemographicKey() {	
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Precinct getPrecinct() {
		return precinct;
	}

	public void setPrecinct(Precinct precint) {
		this.precinct = precint;
	}	
}
