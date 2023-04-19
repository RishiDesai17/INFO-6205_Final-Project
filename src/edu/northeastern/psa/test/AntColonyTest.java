package edu.northeastern.psa.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.northeastern.psa.Ant;
import edu.northeastern.psa.AntColony;
import org.junit.Before;
import org.junit.Test;
//import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class AntColonyTest {

    @Test
    public void testGetHaversineDistance() {
        AntColony antColony = new AntColony("src/edu/northeastern/psa/test/test.csv");
        double lat1 = 42.3601;
        double lat2 = 40.7128;
        double lon1 = -71.0589;
        double lon2 = -74.0060;
        double expectedDistance = 306.96;
        double distance = antColony.getHaversineDistance(lat1, lat2, lon1, lon2);
        assertEquals(expectedDistance, distance, 1);
    }

    @Test
    public void testGetHaversineDistanceWithMock() {
        AntColony antColony = new AntColony("src/edu/northeastern/psa/test/test.csv");
        double lat1 = 42.3601;
        double lat2 = 40.7128;
        double lon1 = -71.0589;
        double lon2 = -74.0060;
        double expectedDistance = 306.96;

        AntColony antColonyMock = mock(AntColony.class);
        when(antColonyMock.getHaversineDistance(lat1, lat2, lon1, lon2)).thenReturn(expectedDistance);

        double distance = antColonyMock.getHaversineDistance(lat1, lat2, lon1, lon2);
        assertEquals(expectedDistance, distance, 0.01);
    }

    @Test
    public void testReadFile() throws IOException {
        AntColony antColony = new AntColony("src/edu/northeastern/psa/test/test.csv");
        int expectedLocationCount = 3;
        assertEquals(expectedLocationCount, antColony.getCoords().size());
    }

    @Test
    public void testTravellingSalesmanWithEmptyGraph() {
        AntColony antColony = new AntColony("src/edu/northeastern/psa/test/empty-file.csv");
        double[][] graph = antColony.getGraph();
        assertEquals(0, graph.length);
    }

    @Test
    public void testGetWeightage() {
        AntColony antColony = new AntColony();
        antColony.setAlpha(1);
        antColony.setBeta(5);
        antColony.setGraph(new double[][]{
                {0, 1, 4},
                {1, 0, 5},
                {4, 5, 0}
        });
        antColony.setRewardMatrix(new double[][]{
                {1, 1.1, 1.2},
                {1.1, 1, 1.3},
                {1.2, 1.3, 1}
        });

        double expectedWeightage = 0.000416;

        double actualWeightage = antColony.getWeightage(1, 2);
        assertEquals(expectedWeightage, actualWeightage, 0.01);
    }

    @Test
    public void testPrintRoute() {
        AntColony antColony = new AntColony();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        List<Integer> route = new ArrayList<>();
        route.add(2);
        route.add(1);
        route.add(4);

        // Call the function that prints to the console
        antColony.printRoute(route);

        // Get the printed output
        String printedOutput = outputStream.toString().trim();

        // Define the expected output
        String expectedOutput = "2-->1-->4-->2";

        // Compare the printed output to the expected output
        assertEquals(expectedOutput, printedOutput);
    }

    @Test
    public void testGetTourCost() {
        AntColony antColony = new AntColony();
        antColony.setGraph(new double[][]{
                {0, 1, 4},
                {1, 0, 5},
                {4, 5, 0}
        });

        Ant ant = new Ant();
        List<Integer> visitedLocations = new ArrayList<>();
        visitedLocations.add(2);
        visitedLocations.add(1);
        visitedLocations.add(0);
        ant.setVisitedLocationsInOrder(visitedLocations);

        double actualCost = antColony.getTourCost(ant);
        assertEquals(10, actualCost, 0);
    }

    @Test
    public void testGenerateRandomVertex() {
        AntColony antColony = new AntColony();
        RandomNumberGenerator randomMock = mock(RandomNumberGenerator.class);
        when(randomMock.nextDouble()).thenReturn(0.5);
        AntColony.setR(randomMock);
        double[][] probabilities = new double[][]{
                {0.4, 1},
                {0.6, 2},
                {0.8, 3}
        };

        Ant mockAnt = mock(Ant.class);
        when(mockAnt.getVisited()).thenReturn(new HashSet<>());
        when(mockAnt.getVisitedLocationsInOrder()).thenReturn(new ArrayList<>());


        int expectedSelectedLocation = 2;
        int actualSelectedLocation = antColony.generateRandomVertex(mockAnt, 1, probabilities);
        assertEquals(expectedSelectedLocation, actualSelectedLocation);

    }

    @Test
    public void testGenerateProbabilities() {
        AntColony antColony = new AntColony();

        AntColony spyColony = spy(antColony);
        spyColony.setGraph(new double[][]{
                {0, 1, 4},
                {1, 0, 5},
                {4, 5, 0}
        });

        doReturn(0.2).when(spyColony).getWeightage(1, 0);
        doReturn(0.8).when(spyColony).getWeightage(1, 2);


        Ant a = new Ant();
        a.setVisited(new HashSet<>());
        double[][] actualProbabilities = spyColony.generateProbabilities(1, a);

        double[][] expectedProbabilities = new double[][]{ {0.2, 0}, {1, 2}, {0, 0} };
        assertArrayEquals(expectedProbabilities, actualProbabilities);
    }

    @Test
    public void testAlterRewardMatrix(){
        AntColony antColony = new AntColony();
        double[][] rewardMatrix = new double[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        };
        antColony.setRewardMatrix(rewardMatrix);

        Ant mockAnt1 = mock(Ant.class);
        when(mockAnt1.getVisitedLocationsInOrder()).thenReturn(new ArrayList<>(Arrays.asList(0, 1)));

        Ant mockAnt2 = mock(Ant.class);
        when(mockAnt2.getVisitedLocationsInOrder()).thenReturn(new ArrayList<>(Arrays.asList(1, 2)));

        AntColony spyColony = spy(antColony);

        doReturn(1.0).when(spyColony).getTourCost(mockAnt1);
        doReturn(1.0).when(spyColony).getTourCost(mockAnt2);

        spyColony.alterRewardMatrix(new Ant[]{mockAnt1, mockAnt2});

        double[][] expectedRewardMatrix = new double[][]{
                {1, 2.9, 0.9},
                {2.9, 1, 2.9},
                {0.9, 2.9, 1},
        };

        assertArrayEquals(expectedRewardMatrix, spyColony.getRewardMatrix());


    }

    @Test
    public void testTravellingSalesman() {
        AntColony antColony = new AntColony();
        antColony.setGraph(new double[][]{
                {0, 3, 5, 4},
                {3, 0, 7, 2},
                {5, 7, 0, 9},
                {4, 2, 9, 0},
        });

        antColony.setRewardMatrix(new double[][]{
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
        });

        double actualMinCost = antColony.travellingSalesman(100,100, new ArrayList<>());
        double expectedMinCost = 18;

        assertEquals(expectedMinCost, actualMinCost, 0.01);

    }

}