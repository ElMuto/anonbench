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
import org.deidentifier.arx.framework.check.INodeChecker;
import org.deidentifier.arx.framework.check.INodeChecker.Result;
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

        public boolean isFulfilled(Node node) {
            switch (config.getType()) {
            case TIME:
                return System.currentTimeMillis() - timestamp >= config.getValue();
            case CHECKS:
                return AlgorithmHeurakles.this.checks >= config.getValue();
            case ANONYMITY:
                return node.hasProperty(Node.PROPERTY_ANONYMOUS);
            default:
                return getGlobalOptimum() != null;
            }
        }

        public void resetTimer() {
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static final int         NODE_PROPERTY_COMPLETED = 1 << 20;
    private static final boolean    PRUNE                   = false;
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
        stopCriterion = new StopCriterion(this.config.getAlgorithm().getTerminationConfig());
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see org.deidentifier.arx.algorithm.AbstractAlgorithm#traverse()
     */
    @Override
    public void traverse() {
        stopCriterion.resetTimer();
        Node bottom = lattice.getBottom();
        assureChecked(bottom);
        if (!stopCriterion.isFulfilled(bottom)) {
            traverse(bottom);
        }
        adjustInfoLoss();
    }

    /**
     * In case of metrics 'DataFly' and 'ImprovedGreedy' the information loss for the global optimum must be calculated based on the metric used by Heurakles, e.g. Loss.
     */
    private void adjustInfoLoss() {
        if (null != getGlobalOptimum() && !config.getDecisionMetric().equals(config.getILMetric())) {
            Result result = checker.check(getGlobalOptimum(), config.getILMetric());
            lattice.updateInformationLoss(getGlobalOptimum(), result.informationLoss);
        }
    }

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
                if (stopCriterion.isFulfilled(successor)) {
                    return;
                }
                if (!successor.hasProperty(NODE_PROPERTY_COMPLETED)) {
                    assureChecked(successor);
                    queue.add(successor);
                }
            }

            Node next;
            while ((next = queue.poll()) != null) {

                if (stopCriterion.isFulfilled(next)) {
                    return;
                }

                if (!next.hasProperty(NODE_PROPERTY_COMPLETED)) {
                    // A node (and it's direct and indirect successors, respectively) can be pruned iff
                    // the information loss metric is monotonic and the nodes's IL is greater or equal than the IL of the
                    // global maximum (regardless of the anonymity criterion's monotonicity)
                    boolean metricMonotonic = checker.getMetric().isMonotonic() &&
                                              checker.getConfiguration().getAbsoluteMaxOutliers() == 0;
                    boolean prune = PRUNE && getGlobalOptimum() != null &&
                                    // depending on monotony of metric we choose to compare either IL of Monotonous Subset with the global Optimum
                                    (metricMonotonic ? next.getInformationLoss() : next.getLowerBound()).compareTo(getGlobalOptimum().getInformationLoss()) >= 0;

                    if (!prune) traverse(next);
                }
            }
        }

        lattice.setProperty(node, NODE_PROPERTY_COMPLETED);
    }
}
