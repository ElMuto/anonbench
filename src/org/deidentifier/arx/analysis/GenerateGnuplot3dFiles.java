package org.deidentifier.arx.analysis;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;

public class GenerateGnuplot3dFiles {
	private static final String resultsFile = "results/resultsTKIL.csv";
	
	private static class Coord3d implements Comparable{
		private final String dataset;
		private final String sa;
		private final Double x;
		private final Double y;
		private final Double z;
		
		public Coord3d(Double x, Double y, Double z, String dataset, String sa) {
			super();
			
			this.dataset = dataset;
			this.sa = sa;
			this.x = x;
			this.y = y;
			this.z = z;
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
			return (x + "\t" + y + "\t" + z);
		}

		private String getPlotName() {
			return getDataset() + "_" + getSa();
		}
		
		public String toString () {
			return (getPlotName() + ": " + printCoordinates());
		}

		@Override
		public int compareTo(Object o) {
			if (this.getX().equals(((Coord3d) o).getX()))
				return this.getY().compareTo(((Coord3d) o).getY());
			else return 0;
		}
	}
	
	public static void main(String[] args) {
		String[][] pairs = new String[][] {
			{"ACS13", "Marital status"},
//			{"ACS13", "Education"},
			};

		for (String[] pair : pairs) {
			try (Stream<String> lines = Files.lines(Paths.get(resultsFile))) {
				lines.
				skip(2).
				map(GenerateGnuplot3dFiles::extractData).
				filter(c -> c.getDataset().equals(pair[0]) && c.getSa().equals(pair[1])).
				filter(c -> c.getX() != null && c.getY() != null).
				sorted().
				forEach(System.out::println);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static Coord3d extractData(String csvLine) {
		int datasetIndex = 3;
		int saIndex = 12;
		int kIndex = 6;
		int tIndex = 9;
		int ilIndex = 21;
		
		NumberFormat nf = NumberFormat.getInstance(Locale.US);		
		String[] resultArray = csvLine.split(";");
		
		Coord3d coord3d = null;
		try {
			coord3d = new Coord3d(
					!"".equals(resultArray[kIndex]) ? nf.parse(resultArray[kIndex]).doubleValue() : null,
					!"".equals(resultArray[tIndex]) ? nf.parse(resultArray[tIndex]).doubleValue() : null,
					nf.parse(resultArray[ilIndex]).doubleValue(),
					resultArray[datasetIndex], resultArray[saIndex]);
		} catch (NumberFormatException | ParseException e) {
			e.printStackTrace();
		} 
		return coord3d;
	}
}
