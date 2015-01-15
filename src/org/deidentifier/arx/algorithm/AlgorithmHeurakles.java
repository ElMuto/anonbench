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
     * This inner class defines 2 stop criteria for an algorithm:
     * it can either stop after a certain number
     * of checked nodes or after a certain number of seconds since
     * the algorithm has started. If both criteria are set, then the
     * algorithm stops after the first condition is true.
     * 
     * @author Helmut Spengler
     * 
     */
    private class StopCriteria {
    	Boolean stop_after_first_anonymous = null;
    	boolean first_anonymous_fulfilled = false;
    	
    	Integer stop_after_num_checks = null;
    	boolean num_checks_fulfilled = false;
    	
    	Integer Stop_after_num_seconds = null;
    	boolean num_seconds_fulfilled = false;
    	
    	/**
    	 * @return information, if the stop criteria are fulfilled
    	 */
    	public boolean stopCriteriaAreFulfilled() {
    		return (first_anonymous_fulfilled || num_seconds_fulfilled || num_checks_fulfilled);
    	}
    	
    	public void setFulfilled(StopCriteriaType stopCriteriaType) {
    		switch (stopCriteriaType) {
			case STOP_AFTER_FIRST_ANONYMOUS:
				first_anonymous_fulfilled = true;
				break;
			case STOP_AFTER_NUM_CHECKS:
				num_checks_fulfilled = true;
				break;
			case STOP_AFTER_NUM_SECONDS:
				num_seconds_fulfilled = true;
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
        
        this.stopCriteria = new StopCriteria();
    }
    
    public AbstractBenchmarkAlgorithm setStopCriterion (StopCriteriaType stopCriteriaType) {
    	if (!stopCriteriaType.equals(StopCriteriaType.STOP_AFTER_FIRST_ANONYMOUS))
    		throw new IllegalArgumentException("Need to supply a parameter");
    	
    	this.stopCriteria.stop_after_first_anonymous = true;
    	
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
    	int x;
        Node[] successors = node.getSuccessors(true);
        if (successors.length > 0) {
            // Build a PriorityQueue based on information loss containing the successors
            PriorityQueue<Node> queue = new PriorityQueue<Node>(successors.length, new InformationLossComparator());
            for (Node successor : successors) {
                if (!successor.hasProperty(PROPERTY_COMPLETED)) {
                    assureChecked(successor);
                    queue.add(successor);
                }
            }
            // Process the successors
            Node next;
            if (getGlobalOptimum() != null)
            	stopCriteria.setFulfilled(StopCriteriaType.STOP_AFTER_FIRST_ANONYMOUS);
            else
            	x = 0;
            
            boolean fulfilled = stopCriteria.stopCriteriaAreFulfilled();
            
            while (!stopCriteria.stopCriteriaAreFulfilled() && (next = queue.peek()) != null) {
//            while (getGlobalOptimum() == null && (next = queue.peek()) != null) {
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
