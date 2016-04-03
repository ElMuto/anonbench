package org.deidentifier.arx.analysis;

import java.io.IOException;
import java.text.ParseException;

public class GenerateAllPlots {
    public static void main(String[] args) throws IOException, ParseException {
    	Analyze_InfluenceOfSingleAttrOnDiff.generateDifficultyInfluencePlots("Plots_influenceOnDifficultyMixed.pdf", null, true, null);
    	Analyze_InfluenceOfSingleAttrOnDiff.generateDifficultyInfluencePlots("Plots_influenceOnDifficultyTypeA.pdf", "A", true, null);
    	Analyze_InfluenceOfSingleAttrOnDiff.generateDifficultyInfluencePlots("Plots_influenceOnDifficultyTypeB.pdf", "B", true, null);
    	Analyze_Compared_Difficulties.generateDifficultyComparisonPlots();
    	System.out.println("done.");
    }
}
