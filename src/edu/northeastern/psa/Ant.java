package edu.northeastern.psa;

import java.util.List;
import java.util.Set;

public class Ant {
	private int currentLocationKey;
	private Set<Integer> visited;
	private List<Integer> visitedLocationsInOrder;
	
	public Ant() {
		
	}

	public Ant(int currentLocationKey, Set<Integer> visited, List<Integer> visitedLocationsInOrder) {
		this.currentLocationKey = currentLocationKey;
		this.visited = visited;
		this.visitedLocationsInOrder = visitedLocationsInOrder;
	}

	public int getCurrentLocationKey() {
		return currentLocationKey;
	}

	public void setCurrentLocationKey(int currentLocationKey) {
		this.currentLocationKey = currentLocationKey;
	}

	public Set<Integer> getVisited() {
		return visited;
	}

	public void setVisited(Set<Integer> visited) {
		this.visited = visited;
	}

	public List<Integer> getVisitedLocationsInOrder() {
		return visitedLocationsInOrder;
	}

	public void setVisitedLocationsInOrder(List<Integer> visitedLocationsInOrder) {
		this.visitedLocationsInOrder = visitedLocationsInOrder;
	}
	
}
