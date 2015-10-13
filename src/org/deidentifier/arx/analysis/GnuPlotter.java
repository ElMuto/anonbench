package org.deidentifier.arx.analysis;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class GnuPlotter {

	protected static Set<File> fileBucket = new HashSet<File>();
    protected static String gnuPlotFileName = "results/commads.plg";

	protected static void executeGnuplot(String gnuPlotFileName) throws IOException {
		System.out.println("Executing gnuplot");
	    ProcessBuilder b = new ProcessBuilder();
	    Process p;
	    b.command("gnuplot", gnuPlotFileName);
	    p = b.start();
	    StreamReader output = new StreamReader(p.getInputStream());
	    StreamReader error = new StreamReader(p.getErrorStream());
	    new Thread(output).start();
	    new Thread(error).start();
	    try {
	    	p.waitFor();
	    } catch (final InterruptedException e) {
	    	throw new IOException(e);
	    }
	
	    if (p.exitValue() != 0) {
	    	throw new IOException("Error executing gnuplot: " + error.getString() + System.lineSeparator() + output.getString());
	    }
	}

	protected static void deleteFilesFromBucket() {
		try {
	    	for (File fileToBeDeleted : fileBucket) {
	    		if(fileToBeDeleted.delete()){
	    			System.out.println(fileToBeDeleted.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
	    	}
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	}

	public GnuPlotter() {
		super();

	    File gnuPlotFile = new File(gnuPlotFileName);
		fileBucket.add(gnuPlotFile);
	}
}