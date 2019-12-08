package model;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.gson.annotations.Expose;

import model.Election.Race;

@Entity
public class Demographic {
	@Expose
	private DemographicKey demographicKey;
	private Precinct precinct;
	@Expose
	private long population;
	
	public Demographic() {
	}
	
	public Demographic(Race race, Precinct precinct, long population) {
		this.demographicKey = new DemographicKey(race, precinct.getId());
		this.precinct = precinct;
		this.population = population;
	}
	
	@Id
	public DemographicKey getDemographicKey() {
		return demographicKey;
	}
	
	public void setDemographicKey(DemographicKey demographicKey) {
		this.demographicKey = demographicKey;
	}
	
	@ManyToOne
	@JoinColumn(name="precinctId", insertable=false, updatable=false)
	public Precinct getPrecinct() {
		return precinct;
	}

	public void setPrecinct(Precinct precint) {
		this.precinct = precint;
	}	
	
	public long getPopulation() {
		return population;
	}
	public void setPopulation(long population) {
		this.population = population;
	}
		
}
