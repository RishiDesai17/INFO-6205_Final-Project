package edu.northeastern.psa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AntColony {
	private double[][] graph;
	private List<Location> coords;
	private double[][] rewardMatrix;
	private double alpha = 1;
	private double beta = 7;
	private double evaporationRate = 0.1;
	private static Random r = new Random();
	private String fileName;

	public double[][] getGraph() {
		return graph;
	}

	public void setGraph(double[][] graph) {
		this.graph = graph;
	}

	public List<Location> getCoords() {
		return coords;
	}

	public void setCoords(List<Location> coords) {
		this.coords = coords;
	}

	public double[][] getRewardMatrix() {
		return rewardMatrix;
	}

	public void setRewardMatrix(double[][] rewardMatrix) {
		this.rewardMatrix = rewardMatrix;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getEvaporationRate() {
		return evaporationRate;
	}

	public void setEvaporationRate(double evaporationRate) {
		this.evaporationRate = evaporationRate;
	}

	public static Random getR() {
		return r;
	}

	public static void setR(Random r) {
		AntColony.r = r;
	}

	public AntColony() {

	}

	public AntColony(String fileName) {
        coords = new ArrayList<>();
		this.fileName = fileName;
        int numLocations = readFile();
        
        if (numLocations == -1) {
        	System.out.println("Something went wrong while reading the file");
        	return;
        }
            
        graph = new double[numLocations][numLocations];
        rewardMatrix = new double[numLocations][numLocations];
        
        for (int i = 0; i < numLocations; i++) {
        	for (int j = 0; j < numLocations; j++) {
        		rewardMatrix[i][j] = 1;
        	}
        }
        
        
        for (int i = 0; i < coords.size(); i++) {
        	double lat1 = coords.get(i).getLat();
            double lon1 = coords.get(i).getLon();
            
        	for (int j = i + 1; j < coords.size(); j++) {
        		double lat2 = coords.get(j).getLat();
                double lon2 = coords.get(j).getLon();
                
                double distance = getHaversineDistance(lat1, lat2, lon1, lon2);
            	
            	graph[i][j] = distance;
            	graph[j][i] = distance;
        	}
        }
	}
	
	public int readFile() {
		String line;
		Set<String> set = new HashSet<>();
		int numLocations = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                String crimeId = columns[0];
                double lat = Double.parseDouble(columns[2]);
                double lon = Double.parseDouble(columns[1]);
                
                String representation = lat + " " + lon;
                if (set.contains(representation)) {
                	continue;
                }
                set.add(representation);

                Location curr = new Location(lat, lon, crimeId);
                coords.add(curr);
                numLocations += 1;
            }
            
            return numLocations;
        } 
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
	}
	
	public double getHaversineDistance(double lat1, double lat2, double lon1, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = c * 6371.01;
        
        return distance;
	}
	
	public double getWeightage(int x, int y) {
		double pheromone = Math.pow(rewardMatrix[x][y], alpha);
		double distance = graph[x][y];
		return pheromone * Math.pow(1.0 / distance, beta);
	}
	
	public double travellingSalesman(int antCount, int genCount, List<String> generationWiseStats) {
		double globalMinCost = 0;
		List<Integer> globalBestRoute = null;
		
		for (int generations = 0; generations < genCount; generations++) {
			Ant[] ants = new Ant[antCount];
			
			double minCost = 0;
			List<Integer> bestRoute = null;
			
			for (int i = 0; i < antCount; i++) {
				int firstVertex = r.nextInt(graph.length);
				
				Set<Integer> visited = new HashSet<>();
				List<Integer> visitedInOrder = new ArrayList<>();
				visited.add(firstVertex);
				visitedInOrder.add(firstVertex);
				
				ants[i] = new Ant(firstVertex, visited, visitedInOrder);
				int currentVertex = firstVertex;
				
				while(ants[i].getVisited().size() < graph.length) {					
					double[][] probabilities = generateProbabilities(currentVertex, ants[i]);

					int selectedLocation = generateRandomVertex(ants[i], currentVertex, probabilities);
					
					currentVertex = selectedLocation;
				}
				
				double currTourCost = getTourCost(ants[i]);
				
				if (bestRoute == null) {
					minCost = currTourCost;
					bestRoute = ants[i].getVisitedLocationsInOrder();
				}
				else if (currTourCost < minCost) {
					minCost = currTourCost;
					bestRoute = ants[i].getVisitedLocationsInOrder();
				}
			}
			System.out.println("Generation : "  + generations + ", minCost = " + minCost);

			generationWiseStats.add((generations + 1) + "," + minCost);
			
			alterRewardMatrix(ants);
			
			if (globalBestRoute == null || minCost < globalMinCost) {
				globalMinCost = minCost;
				globalBestRoute = bestRoute;
			}
		}
		
//		this.printRoute(globalBestRoute);
		System.out.println("Min cost global: " + globalMinCost * 1000 + " meters");
		return globalMinCost;
	}
	
	public void printRoute(List<Integer> route) {
		for (int i = 0; i < route.size(); i++) {
			System.out.print(route.get(i) + "-->");
		}
		System.out.println(route.get(0));
	}
	
	public double getTourCost(Ant ant) {
		double score = 0;
		List<Integer> visitedLocations = ant.getVisitedLocationsInOrder();
		for (int i = 1; i < visitedLocations.size(); i++) {
			score += graph[visitedLocations.get(i)][visitedLocations.get(i - 1)];
		}

		score += graph[visitedLocations.get(0)][visitedLocations.get(visitedLocations.size() - 1)];

		return score;
	}
	
	public int generateRandomVertex(Ant ant, int currentVertex, double[][] probabilities) {
		double randomNumber = r.nextDouble();
		
		int selectedLocation = currentVertex;
		
		for (int nextVertex = 0; nextVertex < probabilities.length; nextVertex++) {
			if (randomNumber <= probabilities[nextVertex][0]) {
				selectedLocation = (int)probabilities[nextVertex][1];
				break;
			}
		}					
		ant.getVisited().add(selectedLocation);
		ant.getVisitedLocationsInOrder().add(selectedLocation);
		
		return selectedLocation;
	}
	
	public double[][] generateProbabilities(int currentVertex, Ant ant) {
		double[][] probabilities = new double[graph.length - ant.getVisited().size()][2];

		double[] row = graph[currentVertex];
		double denominator = 0;
		
		for (int j = 0; j < row.length; j++) {
			if (ant.getVisited().contains(j) || currentVertex == j) {
				continue;
			}
			
			denominator += getWeightage(currentVertex, j);
		}
		
		int idx = 0;
		
		for (int j = 0; j < row.length; j++) {
			if (ant.getVisited().contains(j) || currentVertex == j) {
				continue;
			}
					
			if (idx == 0) {
				probabilities[idx] = new double[] {getWeightage(currentVertex, j) / denominator, j};
			}
			else {
				probabilities[idx] = new double[] {probabilities[idx - 1][0] + (getWeightage(currentVertex, j) / denominator) , j};
			}

			
			idx += 1;
		}
		
		return probabilities;
	}
	
	public void alterRewardMatrix(Ant[] ants) {
		for (int i = 0; i < rewardMatrix.length; i++) {
			for (int j = 0; j < rewardMatrix.length; j++) {
				if (i == j) {
					continue;
				}
				rewardMatrix[i][j] *= (1 - evaporationRate);
			}
		}
		
		for (Ant ant: ants) {
			double score = getTourCost(ant);
			
			List<Integer> visitedLocations = ant.getVisitedLocationsInOrder();
			
			for (int i = 1; i < visitedLocations.size(); i++) {
				rewardMatrix[visitedLocations.get(i)][visitedLocations.get(i - 1)] += 1.0 / score;
				rewardMatrix[visitedLocations.get(i - 1)][visitedLocations.get(i)] += 1.0 / score;
			}
			rewardMatrix[visitedLocations.get(0)][visitedLocations.get(visitedLocations.size() - 1)] += 1.0 / score;
			rewardMatrix[visitedLocations.get(visitedLocations.size() - 1)][visitedLocations.get(0)] += 1.0 / score;
		}
	}
	

}
