package org.deidentifier.arx.analysis;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateGnuplot3dFiles {
	private static final String resultsFile = "results/resultsTKIL.csv";
	
	private static class Coord3d implements Comparable<Coord3d>{
		private final String dataset;
		private final String sa;
		private final String ilMeasure;
		private final Double x;
		private final Double y;
		private final Double z;
		
		public Coord3d(Double x, Double y, Double z, String dataset, String sa, String ilMeasure) {
			super();
			
			this.dataset = dataset;
			this.sa = sa;
			this.ilMeasure = ilMeasure;
			
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String getIlMeasure() {
			return ilMeasure;
		}

		public String getDataset() {
			return dataset;
		}

		public String getSa() {
			return sa;
		}

		public Double getX() {
			return x;
		}

		public Double getY() {
			return y;
		}

		public Double getZ() {
			return z;
		}
		
		public String printCoordinates() {
			return (getX() + "\t" + getY() + "\t" + getZ());
		}
		
		public String toString () {
			return (printCoordinates());
		}

		@Override
		public int compareTo(Coord3d o) {
			if (this.getX().equals(o.getX()))
				return this.getY().compareTo(o.getY());
			else return 0;
		}
	}
	
	public static void main(String[] args) {
		String[][] pairs = new String[][] {
//			{"ACS13", "Marital status", "Loss"},
			{"ACS13", "Education", "Loss"},
			};

		for (String[] pair : pairs) {
			try (Stream<String> lines = Files.lines(Paths.get(resultsFile))) {
				List<Coord3d> coordList =
				lines.
				skip(2).
				map(GenerateGnuplot3dFiles::extractData).
				filter(c -> c.getDataset().equals(pair[0]) && c.getSa().equals(pair[1]) && c.getIlMeasure().equals(pair[2])).
				filter(c -> c.getX() != null && c.getY() != null).
				sorted().
				collect(Collectors.toList());

				System.out.println(printWithEmptyLines(coordList));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static Coord3d extractData(String csvLine) {
		int ilMeasureIndex = 1;
		int datasetIndex = 3;
		int saIndex = 12;
		int kIndex = 6;
		int tIndex = 9;
		int ilValIndex = 21;
		
		NumberFormat nf = NumberFormat.getInstance(Locale.US);		
		String[] resultArray = csvLine.split(";");
		
		Coord3d coord3d = null;
		try {
			coord3d = new Coord3d(
					!"".equals(resultArray[kIndex]) ? nf.parse(resultArray[kIndex]).doubleValue() : null,
					!"".equals(resultArray[tIndex]) ? nf.parse(resultArray[tIndex]).doubleValue() : null,
					nf.parse(resultArray[ilValIndex]).doubleValue(),
					resultArray[datasetIndex], resultArray[saIndex], resultArray[ilMeasureIndex]);
		} catch (NumberFormatException | ParseException e) {
			e.printStackTrace();
		} 
		return coord3d;
	}
	
	private static String printWithEmptyLines(List<Coord3d> coords) {
		String ret = "";
		
		Double lastX = coords.get(0).getX();
		
		for (Coord3d coord : coords) {
			if (!lastX.equals(coord.getX()))
				ret += "\n";
			ret += coord + "\n";
			lastX = coord.getX();
		}
		
		return ret;
	}
}
