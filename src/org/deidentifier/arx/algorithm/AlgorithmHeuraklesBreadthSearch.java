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

import org.deidentifier.arx.BenchmarkConfiguration.AnonConfiguration;
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
public class AlgorithmHeuraklesBreadthSearch extends AbstractBenchmarkAlgorithm {

    private class StopCriterion {
        private final TerminationConfiguration config;
        private long                           timestamp = System.currentTimeMillis();

        public StopCriterion(TerminationConfiguration config) {
            this.config = config;
        }

        public boolean isFulfilled() {
            switch (config.getType()) {
            case TIME:
                return System.currentTimeMillis() - timestamp >= config.getValue();
            case CHECKS:
                return AlgorithmHeuraklesBreadthSearch.this.checks >= config.getValue();
            case ANONYMITY:
                return getGlobalOptimum() != null;
            default:
                throw new IllegalStateException("Unknown termination condition");
            }
        }

        public void resetTimer() {
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static final int          MAX_QUEUE_SIZE          = 50000;
    public static final int           NODE_PROPERTY_COMPLETED = 1 << 20;
    private static boolean            PRUNE_LOWER_BOUND       = false;
    private final StopCriterion       stopCriterion;
    private final AnonConfiguration   config;

    /**
     * Creates a new instance of the heurakles algorithm.
     * 
     * @param lattice The lattice
     * @param checker The checker
     * @param config The config
     * 
     */
    public AlgorithmHeuraklesBreadthSearch(AbstractLattice lattice, INodeChecker checker, AnonConfiguration config) {
        super(lattice, checker);
        checker.getHistory().setStorageTrigger(History.STORAGE_TRIGGER_ALL);
        this.config = config;
        this.stopCriterion = new StopCriterion(this.config.getAlgorithm().getTerminationConfig());
    }

    /**
     * Makes sure that the given node has been checked
     * @param node
     */
    private void assureChecked(final Node node) {
        if (!node.hasProperty(Node.PROPERTY_CHECKED)) {
            check(node);
        }
    }

    /**
     * Returns whether information loss measure should be forced or not
     * @return
     */
    @Override
    protected boolean isForceMeasureInfoLossRequired() {
        return true;
    }

    /**
     * Returns whether this algorithm requires a materialized lattice.
     * @return
     */
    public boolean isMaterializedLatticeRequired() {
        return false;
    }

    @Override
    public void traverse() {
        
        MinMaxPriorityQueue<Node> _queue = new MinMaxPriorityQueue<Node>(MAX_QUEUE_SIZE, new Comparator<Node>() {
            @Override
            public int compare(Node arg0, Node arg1) {
                return arg0.getInformationLoss().compareTo(arg1.getInformationLoss());
            }
        });
        
        stopCriterion.resetTimer();
        Node bottom = lattice.getBottom();
        assureChecked(bottom);
        if (stopCriterion.isFulfilled()) {
            latticeCompleted = false;
            return;
        }
        _queue.add(bottom);
        
        Node next;
        while ((next = _queue.poll()) != null) {

            // A node (and it's direct and indirect successors, respectively) can be pruned if
            // the information loss is monotonic and the nodes's IL is greater or equal than the IL of the
            // global maximum (regardless of the anonymity criterion's monotonicity)
            boolean metricMonotonic = checker.getMetric().isMonotonic() || checker.getConfiguration().getAbsoluteMaxOutliers() == 0;

            // Depending on monotony of metric we choose to compare either IL or monotonic subset with the global optimum
            boolean prune = false;
            if (getGlobalOptimum() != null) {
                if (metricMonotonic) prune = next.getInformationLoss().compareTo(getGlobalOptimum().getInformationLoss()) >= 0;
                else if (PRUNE_LOWER_BOUND) prune = next.getLowerBound().compareTo(getGlobalOptimum().getInformationLoss()) >= 0;
            }

            if (!prune && !next.hasProperty(NODE_PROPERTY_COMPLETED)) {

                for (Node successor : next.getSuccessors(true)) {
                    
                    if (!successor.hasProperty(NODE_PROPERTY_COMPLETED)) {
                        assureChecked(successor);
                        _queue.add(successor);
                    }
                    
                    while (_queue.size() > MAX_QUEUE_SIZE) {
                        _queue.removeTail();
                    }

                    if (stopCriterion.isFulfilled()) {
                        latticeCompleted = false;
                        return;
                    }
                }
                
                lattice.setProperty(next, NODE_PROPERTY_COMPLETED);
            }
        }
        
        latticeCompleted = true;
    }
}
