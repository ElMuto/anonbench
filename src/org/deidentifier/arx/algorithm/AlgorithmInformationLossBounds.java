/*
 * ARX: Powerful Data Anonymization
 * Copyright (C) 2012 - 2014 Florian Kohlmayer, Fabian Prasser
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

import org.deidentifier.arx.framework.check.INodeChecker;
import org.deidentifier.arx.framework.lattice.MaterializedLattice;
import org.deidentifier.arx.framework.lattice.Node;

/**
 * This class provides a DFS algorithm which traverses the whole search space and tracks the transformations with minimal and maximal information loss, respectively.
 *
 * @author Johanna Eicher
 */
public class AlgorithmInformationLossBounds extends AbstractBenchmarkAlgorithm {

    private Node globalMinimum;
    private Node globalMaximum;

    public AlgorithmInformationLossBounds(final MaterializedLattice lattice, final INodeChecker checker) {
        super(lattice, checker);
    }

    /**
     * Entry point into the algorithm.
     */
    @Override
    public void traverse() {
        traverse(lattice.getBottom());
    }

    /**
     * Traversing over the whole search space, i.e. each node, in order to check the node and determine whether its information loss provides either an upper or a lower bound. This is done recursively until each node has been visited and is checked.
     * @param node
     */
    private void traverse(final Node node) {

        check(node);

        Node[] successors = node.getSuccessors(true);
        for (int i = 0; i < successors.length; i++) {
            Node successor = successors[i];
            if (!successor.hasProperty(Node.PROPERTY_CHECKED)) {
                traverse(successor);
            }
        }
    }

    @Override
    protected void check(Node node) {
        lattice.setChecked(node, checker.check(node, true));
        trackMinimum(node);
        trackMaximum(node);
    }

    /**
     * In case the node is anonymous, determine whether the information loss of this node is a global maximum.
     * @param node
     */
    private void trackMaximum(Node node) {
        if (node.hasProperty(Node.PROPERTY_ANONYMOUS) &&
            ((globalMaximum == null) ||
            (node.getInformationLoss().compareTo(globalMaximum.getInformationLoss()) > 0))) {
            globalMaximum = node;
        }

    }

    /**
     * In case the node is anonymous, determine whether the information loss of this node is a global minimum.
     * @param node
     */
    private void trackMinimum(Node node) {
        if (node.hasProperty(Node.PROPERTY_ANONYMOUS) &&
            ((globalMinimum == null) ||
            (node.getInformationLoss().compareTo(globalMinimum.getInformationLoss()) < 0))) {
            globalMinimum = node;
        }

    }

    /**
     * @return the Node with the globally minimal information loss
     */
    public Node getGlobalMinimum() {
        return globalMinimum;
    }

    /**
     * @return the Node with the globally maximal information loss
     */
    public Node getGlobalMaximum() {
        return globalMaximum;
    }

}
