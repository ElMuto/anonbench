import java.io.IOException;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXLattice.Anonymity;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;

public class ReproduceOutOfMemoryError {
	public static void main(String[] args) throws IOException {
		
		String sa = "Birthplace";
		String[] qis = new String[] { "Age", "Region", "Marital status", "Labor force status", "Citizenship status" };
		String dataset = "atus";

		Data arxData = Data.create("data/" + dataset + ".csv",  ';');
		for (String qi : qis) {
			arxData.getDefinition().setAttributeType(qi, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
			arxData.getDefinition().setHierarchy(qi, Hierarchy.create("hierarchies/" + dataset + "_hierarchy_" + qi + ".csv", ';'));
		}
		arxData.getDefinition().setAttributeType(sa, AttributeType.SENSITIVE_ATTRIBUTE);

		ARXConfiguration config = ARXConfiguration.create();
		config.setMetric(Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN));
		config.addCriterion(new HierarchicalDistanceTCloseness(sa, 0.2d, Hierarchy.create("hierarchies/" + dataset + "_hierarchy_" + sa + ".csv", ';')));
		config.setMaxOutliers(0.05d);

		ARXAnonymizer anonymizer = new ARXAnonymizer();
		ARXResult result = anonymizer.anonymize(arxData, config);		

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
        
        System.out.println(1- (((double) numSolutions) / ((double) lattice.getSize())));
	}
}
