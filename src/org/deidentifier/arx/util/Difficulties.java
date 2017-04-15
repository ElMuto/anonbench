package org.deidentifier.arx.util;

import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXLattice.Anonymity;
import org.deidentifier.arx.ARXResult;

public class Difficulties {

	public static double calculateDifficulty(ARXResult result) {
	    int numSolutions = 0;
	    ARXLattice lattice = result.getLattice();
	    for (ARXNode[] level : lattice.getLevels()) {
	        for (ARXNode node : level) {
	     
	        	// Make sure that every transformation is classified correctly
	        	if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
	        		result.getOutput(node).release();
	        	}
	        	
	        	if (node.getAnonymity() == Anonymity.ANONYMOUS) {
	        		numSolutions++;
	        		
	        	// Sanity check
	        	} else if (node.getAnonymity() != Anonymity.NOT_ANONYMOUS) {
	            	throw new RuntimeException("Solution space is still not classified completely");   
	            }
	        }
	    }
	    
	    return 1- (((double) numSolutions) / ((double) lattice.getSize()));
	}

}
