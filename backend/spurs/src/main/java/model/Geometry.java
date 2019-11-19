package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Geometry {
	@Id
	private long id;
	@OneToMany
	private List<Coordinate> coordinates;
	
	public Geometry() {
	}
	
	public Geometry(long id) {
		this.id = id;
	}
	
}
