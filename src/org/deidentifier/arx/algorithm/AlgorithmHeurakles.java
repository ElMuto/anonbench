/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal
 * methods for the de-identification of biomedical data"
 * 
 * Copyright (C) 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.deidentifier.arx.algorithm;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.deidentifier.arx.framework.check.INodeChecker;
import org.deidentifier.arx.framework.check.history.History;
import org.deidentifier.arx.framework.lattice.AbstractLattice;
import org.deidentifier.arx.framework.lattice.Node;

/**
 * This class implements a simple depth-first-search with an outer loop.
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class AlgorithmHeurakles extends AbstractBenchmarkAlgorithm {

    public static final int PROPERTY_COMPLETED = 1 << 20;
    
    public enum StopCriteriaType {
    	STOP_AFTER_FIRST_ANONYMOUS,
    	STOP_AFTER_NUM_SECONDS,
    	STOP_AFTER_NUM_CHECKS
    }
    
    private static StopCriteria stopCriteria;

    /**
     * Auxiliary class for comparing nodes based on their information loss
     * 
     * @author Raffael Bild
     * 
     */
    public class InformationLossComparator implements Comparator<Node>
    {
        @Override
        public int compare(Node x, Node y)
        {
            return x.getInformationLoss().compareTo(y.getInformationLoss());
        }
    }

    
    /**
     * This inner class defines 3 stop criteria for an algorithm:
     * it can either stop after a certain number or after the first anonymous node
     * has been found. If more than one criterion is set, then the
     * algorithm stops after the first condition is true.
     * 
     * @author Helmut Spengler
     * 
     */
    private class StopCriteria {
    	private Boolean stopAfterFirstAnonymous = null;
    	private boolean firstAnonymousFulfilled = false;
    	
    	private Integer stopAfterNumChecks = null;
    	private boolean numChecksFulfilled = false;
    	
    	private Integer stopAfterNumSeconds = null;
    	private boolean numSecondsFulfilled = false;    	
    	
    	/**
    	 * @return true, after the first of the <B>activated</B> stop criteria are fulfilled
    	 */
    	public boolean stopCriteriaAreFulfilled() {    		
    		if (stopAfterNumChecks != null && checks >= stopAfterNumChecks)
    			numChecksFulfilled = true;    		

    		final boolean printFulfillmentStatus = false;
    		if (printFulfillmentStatus) {
    			if (stopAfterFirstAnonymous != null && firstAnonymousFulfilled)
    				System.out.println("FIRST_ANONYMOUS stop criterion is fulfilled");

    			if (stopAfterNumChecks != null && numChecksFulfilled)
    				System.out.println("NUM_CHECKS stop criterion (n = " + checks + ") is fulfilled");

    			if (stopAfterNumSeconds != null && numSecondsFulfilled)
    				System.out.println("NUM_SECONDS stop criterion is fulfilled");
    		}
    		
    		return 
    				(stopAfterFirstAnonymous != null ? firstAnonymousFulfilled : false) || 
    				(stopAfterNumChecks != null ? numChecksFulfilled : false) ||
    				(stopAfterNumSeconds != null ? numSecondsFulfilled : false);
    	}
    	
    	public void setFulfilled(StopCriteriaType stopCriteriaType) {
    		switch (stopCriteriaType) {
			case STOP_AFTER_FIRST_ANONYMOUS:
				firstAnonymousFulfilled = true;
				break;
			case STOP_AFTER_NUM_CHECKS:
				numChecksFulfilled = true;
				break;
			case STOP_AFTER_NUM_SECONDS:
				numSecondsFulfilled = true;
				break;
			default:
				break;    			
    		}
    	}
    }
    

    /**
     * Creates a new instance of the heurakles algorithm.
     * 
     * @param lattice The lattice
     * @param checker The checker
     */
    public AlgorithmHeurakles(final AbstractLattice lattice, final INodeChecker checker) {
        super(lattice, checker);
        // Set strategy
        checker.getHistory().setStorageTrigger(History.STORAGE_TRIGGER_ALL);
        
        stopCriteria = new StopCriteria();
    }
    
    
    /**
     * Define and activate the stop criterion STOP_AFTER_FIRST_ANONYMOUS. After this method is
     * called with this parameter, the algorithm will stop, after the first anonymous node has been found.
     * 
     * @param stopCriteriaType STOP_AFTER_FIRST_ANONYMOUS is the only allowed criterion for this method.
     * @return the algorithm object itself for supporting chained method calls
     */
    public AbstractBenchmarkAlgorithm setStopCriterion (StopCriteriaType stopCriteriaType) {
    	if (!stopCriteriaType.equals(StopCriteriaType.STOP_AFTER_FIRST_ANONYMOUS))
    		throw new IllegalArgumentException("Need to supply the number of checks/seconds as a second parameter");
    	
    	stopCriteria.stopAfterFirstAnonymous = true;    	
    	return this;
    }
    
    /**
     * Define and activate a stop criterion that depends on the reaching of a positive
     * integer threshold. If this method is is called, the algorithm will stop, after
     * this threshold has been reached.
     * 
     * @param stopCriteriaType currently only STOP_AFTER_NUM_CHECKS is supported. STOP_AFTER_NUM_SECONDS will follow shortly.
     * @param num positive Integer defining the threshold
     * @return the algorithm object itself for supporting chained method calls
     */
    public AbstractBenchmarkAlgorithm setStopCriterion (StopCriteriaType stopCriteriaType, int num) {
    	if (!stopCriteriaType.equals(StopCriteriaType.STOP_AFTER_NUM_CHECKS))
    		throw new IllegalArgumentException("only STOP_AFTER_NUM_CHECKS is supported so far");
    	if (num < 0)
    		throw new IllegalArgumentException("num must be greater than 0");
    	
    	stopCriteria.stopAfterNumChecks = num;    	
    	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deidentifier.arx.algorithm.AbstractAlgorithm#traverse()
     */
    @Override
    public void traverse() {
        Node bottom = lattice.getBottom();
        assureChecked(bottom);
        if (getGlobalOptimum() == null) traverse(bottom);
    }

    private void traverse(final Node node) {
        Node[] successors = node.getSuccessors(true);
        if (successors.length > 0) {
            // Build a PriorityQueue based on information loss containing the successors
            PriorityQueue<Node> queue = new PriorityQueue<Node>(successors.length, new InformationLossComparator());
            for (Node successor : successors) {
            	if (stopCriteria.stopCriteriaAreFulfilled())
            		break;
                if (!successor.hasProperty(PROPERTY_COMPLETED)) {
                    assureChecked(successor);
                    queue.add(successor);
                }
            }
            
            // Process the successors
            if (getGlobalOptimum() != null)
            	stopCriteria.setFulfilled(StopCriteriaType.STOP_AFTER_FIRST_ANONYMOUS);

            Node next;
            while ((next = queue.peek()) != null) {
            	if (stopCriteria.stopCriteriaAreFulfilled())
            		break;
                if (!next.hasProperty(PROPERTY_COMPLETED)) {
                    traverse(next);
                }
                queue.poll();
            }
        }

        lattice.setProperty(node, PROPERTY_COMPLETED);
    }

    private void assureChecked(final Node node) {
        if (!node.hasProperty(Node.PROPERTY_CHECKED)) {
        	check(node);
//            lattice.setChecked(node, checker.check(node, true));
//            trackOptimum(node);
        }
    }

    /**
     * Returns whether this algorithm requires a materialized lattice.
     * @return
     */
    public boolean isMaterializedLatticeRequired() {
        return false;
    }
    
    /**
     * Returns whether information loss measure should be forced or not
     * @return
     */
    @Override
    protected boolean isForceMeasureInfoLossRequired() {
    	return true;
    }
}
