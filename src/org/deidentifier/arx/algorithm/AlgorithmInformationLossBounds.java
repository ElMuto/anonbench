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

    private int  counter;

    public AlgorithmInformationLossBounds(final MaterializedLattice lattice, final INodeChecker checker) {
        super(lattice, checker);
    }

    @Override
    public void traverse() {
        System.out.println("Lattice Size: " + getLatticeSize());
        counter = 1;
        Node bottom = lattice.getBottom();
        lattice.setChecked(bottom, checker.check(bottom, true));

        traverse(bottom);

    }

    private void traverse(final Node node) {
        Node[] successors = node.getSuccessors(true);
        for (int i = 0; i < successors.length; i++) {
            Node successor = successors[i];
            if (!successor.hasProperty(Node.PROPERTY_CHECKED)) {
                check(successor);
                traverse(successor);
            }
        }
    }

    @Override
    protected void check(Node node) {
        counter++;
        if (counter % 1000 == 0 || counter == getLatticeSize()) {
            System.out.println("Done with " + counter + "/" + getLatticeSize());
        }
        lattice.setChecked(node, checker.check(node, true));
        trackMinimum(node);
        trackMaximum(node);
    }

    private void trackMaximum(Node node) {
        if (node.hasProperty(Node.PROPERTY_ANONYMOUS) &&
            ((globalMaximum == null) ||
             (node.getInformationLoss().compareTo(globalMaximum.getInformationLoss()) > 0) ||
            ((node.getInformationLoss().compareTo(globalMaximum.getInformationLoss()) == 0) && (node.getLevel() > globalMaximum.getLevel())))) {
            globalMaximum = node;
        }

    }

    private void trackMinimum(Node node) {
        if (node.hasProperty(Node.PROPERTY_ANONYMOUS) &&
            ((globalMinimum == null) ||
             (node.getInformationLoss().compareTo(globalMinimum.getInformationLoss()) < 0) ||
            ((node.getInformationLoss().compareTo(globalMinimum.getInformationLoss()) == 0) && (node.getLevel() < globalMinimum.getLevel())))) {
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
