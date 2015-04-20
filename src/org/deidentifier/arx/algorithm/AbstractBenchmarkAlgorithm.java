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

import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.framework.check.INodeChecker;
import org.deidentifier.arx.framework.check.StateMachine.TransitionType;
import org.deidentifier.arx.framework.lattice.AbstractLattice;
import org.deidentifier.arx.framework.lattice.Node;
import org.deidentifier.arx.metric.InformationLoss;

/**
 * Abstract base class for algorithms used in the benchmark
 * @author Fabian Prasser
 */
public abstract class AbstractBenchmarkAlgorithm extends AbstractAlgorithm {

    /** The number of rollups that could have been performed */
    protected int   rollups;
    /** The number of checks */
    protected int   checks;
    /** The number of snapshot-based optimizations */
    protected int   snapshots;
    /** The node checked previously */
    protected Node  previous;
    /** The hierarchy heights for each QI. */
    protected int[] hierarchyHeights;
    /** The information losses of the optima which have been found. */
    protected List<String> optimaInformationLosses = new ArrayList<String>();
    /** The timestamps indicating when an optimum has been found in nanoseconds. */
    protected List<Long> optimumCheckedTimestamps = new ArrayList<Long>();
    /** Flag indicating if the complete lattice has been processed. */
    boolean latticeCompleted = true;

    /**
     * Constructor
     * @param lattice
     * @param checker
     */
    protected AbstractBenchmarkAlgorithm(AbstractLattice lattice, INodeChecker checker) {
        super(lattice, checker);
        this.hierarchyHeights = lattice.getTop().getTransformation().clone();
        for (int i = 0; i < hierarchyHeights.length; i++) {
            this.hierarchyHeights[i]++;
        }
    }

    /**
     * Returns the number of checks
     * @return
     */
    public int getNumChecks() {
        return checks;
    }

    /**
     * Returns the number of potential rollups
     * @return
     */
    public int getNumRollups() {
        return rollups;
    }

    /**
     * Returns the number of snapshot-based optimizations applied
     * @return
     */
    public int getNumSnapshots() {
        return snapshots;
    }
    
    /**
     * Returns the information losses of the optima which have been found
     *
     * @return
     */
    public List<String> getOptimaInformationLosses() {
        return optimaInformationLosses;
    }
    
    /**
     * Returns the timestamps indicating when the optima have been found in  nanoseconds
     *
     * @return
     */
    public List<Long> getOptimumCheckedTimestamps() {
        return optimumCheckedTimestamps;
    }
    
    /**
     * Returns the total size of the lattice
     * @return
     */
    public long getTotalLatticeSize() {
        long size = 1;
        for (int height: hierarchyHeights)
            size *= height;
        return size;
    }
    
    /**
     * Returns if the complete lattice has been processed
     * @return
     */
    public boolean getLatticeCompleted() {
        return latticeCompleted;
    }

    /**
     * Performs a check and keeps track of potential rollups
     * @param node
     */
    @SuppressWarnings("unused")
    protected void check(Node node) {

        // Check
        boolean forceMeasureInfoLoss = isForceMeasureInfoLossRequired();
        lattice.setChecked(node, checker.check(node, forceMeasureInfoLoss));
        
        Node oldGlobalOptimum = getGlobalOptimum();
        trackOptimum(node);
        Node newGlobalOptimum = getGlobalOptimum();
        
        if (oldGlobalOptimum != newGlobalOptimum) {
            if (BenchmarkSetup.RECORD_ALL_OPTIMA || optimaInformationLosses.size() == 0)   {
                optimaInformationLosses.add(newGlobalOptimum.getInformationLoss().toString());
                optimumCheckedTimestamps.add(System.nanoTime());
            } else {
                optimaInformationLosses.set(0, newGlobalOptimum.getInformationLoss().toString());
                optimumCheckedTimestamps.set(0,System.nanoTime());
            }
        }
        
        checks++;
        snapshots += (checker.getTransitionType() == TransitionType.SNAPSHOT) ? 1 : 0;

        // Store
        if (previous == null) {
            previous = node;
            return;
        }

        // Check if successor
        boolean successor = true;
        for (int i = 0; i < node.getTransformation().length; i++) {
            if (node.getTransformation()[i] < previous.getTransformation()[i]) {
                successor = false;
            }
        }

        previous = node;

        // Count
        if (successor) {
            rollups++;
        }
    }

    /**
     * Returns whether the transformation represented by the node was
     * determined to be anonymous. Returns <code>null</code> if such information
     * is not available
     * @param node
     * @return
     */
    protected Boolean isAnonymous(Node node) {
        if (node.hasProperty(Node.PROPERTY_ANONYMOUS)) {
            return true;
        } else if (node.hasProperty(Node.PROPERTY_NOT_ANONYMOUS)) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * Returns whether the node has been tagged already
     * @param node
     * @return
     */
    protected boolean isTagged(Node node) {
        return node.hasProperty(Node.PROPERTY_ANONYMOUS) ||
               node.hasProperty(Node.PROPERTY_NOT_ANONYMOUS);
    }

    /**
     * Tags a transformation
     * @param node
     * @param lattice
     * @param anonymous
     */
    protected void setAnonymous(AbstractLattice lattice, Node node, boolean anonymous) {
        if (anonymous) {
            lattice.setProperty(node, Node.PROPERTY_ANONYMOUS);
        } else {
            lattice.setProperty(node, Node.PROPERTY_NOT_ANONYMOUS);
        }
    }

    /**
     * Tags a transformation
     * @param node
     * @param anonymous
     */
    protected void setAnonymous(Node node, boolean anonymous) {
        setAnonymous(lattice, node, anonymous);
    }

    /**
     * Predictively tags the search space with the node's anonymity property
     * @param node
     * @param lattice
     */
    protected void tag(AbstractLattice lattice, Node node) {
        if (node.hasProperty(Node.PROPERTY_ANONYMOUS)) {
            tagAnonymous(lattice, node);
        }
        else if (node.hasProperty(Node.PROPERTY_NOT_ANONYMOUS)) {
            tagNotAnonymous(lattice, node);
        }
    }

    /**
     * Predictively tags the search space with the node's anonymity property
     * @param node
     */
    protected void tag(Node node) {
        tag(lattice, node);
    }

    /**
     * Predictively tags the search space from an anonymous transformation
     * @param node
     * @param lattice
     */
    protected void tagAnonymous(AbstractLattice lattice, Node node) {
        lattice.setPropertyUpwards(node, true, Node.PROPERTY_ANONYMOUS |
                                               Node.PROPERTY_SUCCESSORS_PRUNED);
    }

    /**
     * Predictively tags the search space from an anonymous transformation
     * @param node
     */
    protected void tagAnonymous(Node node) {
        tagAnonymous(lattice, node);
    }

    /**
     * Predictively tags the search space from a non-anonymous transformation
     * @param node
     * @param lattice
     */
    protected void tagNotAnonymous(AbstractLattice lattice, Node node) {
        lattice.setPropertyDownwards(node, false, Node.PROPERTY_NOT_ANONYMOUS);
    }

    /**
     * Predictively tags the search space from a non-anonymous transformation
     * @param node
     */
    protected void tagNotAnonymous(Node node) {
        tagNotAnonymous(lattice, node);
    }

    /**
     * Returns whether this algorithm requires a materialized lattice.
     * @return
     */
    @Override
    public boolean isMaterializedLatticeRequired() {
        return true;
    }

    /**
     * Returns whether information loss measure should be forced or not
     * @return
     */
    protected boolean isForceMeasureInfoLossRequired() {
        return false;
    }

    /**
     * Returns the information loss of the given node
     * @param node
     * @return
     */
    public InformationLoss<?> getInformationLoss(Node node) {
        node.setData(null);
        return checker.check(node, true).informationLoss;
    }
}
