package model;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Demographic {
	
	@Id
	private DemographicKey demographicKey;
	private long population;
	
	
	public DemographicKey getDemographicKey() {
		return demographicKey;
	}
	
	public void setDemographicKey(DemographicKey demographicKey) {
		this.demographicKey = demographicKey;
	}
	
	public long getPopulation() {
		return population;
	}
	public void setPopulation(long population) {
		this.population = population;
	}
		
}
