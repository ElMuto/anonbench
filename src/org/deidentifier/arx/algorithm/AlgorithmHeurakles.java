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

import org.deidentifier.arx.BenchmarkConfiguration.AnonConfiguration;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
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
                return AlgorithmHeurakles.this.checks >= config.getValue();
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

    public static final int         NODE_PROPERTY_COMPLETED = 1 << 20;
    private final StopCriterion     stopCriterion;
    private final AnonConfiguration config;

    /**
     * Creates a new instance of the heurakles algorithm.
     * 
     * @param lattice The lattice
     * @param checker The checker
     * @param config The config
     * 
     */
    public AlgorithmHeurakles(AbstractLattice lattice, INodeChecker checker, AnonConfiguration config) {
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
        stopCriterion.resetTimer();
        Node bottom = lattice.getBottom();
        assureChecked(bottom);
        if (!stopCriterion.isFulfilled()) {
            traverse(bottom);
        }
    }

    /**
     * Traverse
     * @param node
     */
    private void traverse(final Node node) {
        Node[] successors = node.getSuccessors(true);
        if (successors.length > 0) {
            // Build a PriorityQueue based on information loss containing the successors
            PriorityQueue<Node> queue = new PriorityQueue<Node>(successors.length, new Comparator<Node>() {
                @Override
                public int compare(Node arg0, Node arg1) {
                    return arg0.getInformationLoss().compareTo(arg1.getInformationLoss());
                }
            });
            for (Node successor : successors) {
                if (!successor.hasProperty(NODE_PROPERTY_COMPLETED)) {
                    assureChecked(successor);
                    queue.add(successor);
                }
                // FIXME is this correct?
                if (AlgorithmType.IMPROVED_GREEDY != this.config.getAlgorithm().getType()) {
                    if (stopCriterion.isFulfilled()) {
                        return;
                    }
                }
            }

            Node next;
            while ((next = queue.poll()) != null) {

                if (!next.hasProperty(NODE_PROPERTY_COMPLETED)) {

                    if (stopCriterion.isFulfilled()) {
                        return;
                    }

                    // A node (and it's direct and indirect successors, respectively) can be pruned iff
                    // the information loss is monotonic and the nodes's IL is greater or equal than the IL of the
                    // global maximum (regardless of the anonymity criterion's monotonicity)
                    boolean metricMonotonic = checker.getMetric().isMonotonic() || checker.getConfiguration().getAbsoluteMaxOutliers() == 0;

                    // Depending on monotony of metric we choose to compare either IL of monotonic subset with the global optimum
                    boolean prune = getGlobalOptimum() != null &&
                                    (metricMonotonic ? next.getInformationLoss() : next.getLowerBound()).compareTo(getGlobalOptimum().getInformationLoss()) >= 0;

                    // Next
                    if (!prune) traverse(next);
                }
            }
        }

        lattice.setProperty(node, NODE_PROPERTY_COMPLETED);
    }
}
