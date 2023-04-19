package edu.northeastern.psa;

//import com.github.davidmoten.geo.GeoHash;
//import com.github.davidmoten.geo.LatLong;
//import com.github.davidmoten.geo.LatLongDistance;
//import com.github.davidmoten.geo.MercatorProjection;
//import com.github.davidmoten.geo.Projections;
//import com.github.davidmoten.geo.geojson.Feature;
//import com.github.davidmoten.geo.geojson.FeatureCollection;
//import com.github.davidmoten.geo.geojson.GeoJson;
//import com.github.davidmoten.geo.geojson.Geometry;
//import com.github.davidmoten.geo.geojson.LineString;
//import com.github.davidmoten.geo.geojson.Point;
//import com.github.davidmoten.geo.geojson.Properties;
//import com.github.davidmoten.geo.geojson.PropertyMap;
//import com.github.davidmoten.geo.geojson.Writer;

import java.util.ArrayList;

public class Main/* extends Application */{
	public static void main(String args[]) {
		AntColony a = new AntColony("src/edu/northeastern/psa/file.csv");
		a.travellingSalesman(50,100, new ArrayList<>());
		System.out.println();
	}
}
