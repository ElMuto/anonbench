package org.deidentifier.arx.analysis;

import java.io.IOException;
import java.text.ParseException;

public class GenerateAllPlots {
    public static void main(String[] args) throws IOException, ParseException {
    	Analyze_IOD.generateDifficultyInfluencePlots("Plots_influenceOnDifficultyMixed.pdf", null, true);
    	Analyze_IOD.generateDifficultyInfluencePlots("Plots_influenceOnDifficultyTypeA.pdf", "A", true);
    	Analyze_IOD.generateDifficultyInfluencePlots("Plots_influenceOnDifficultyTypeB.pdf", "B", true);
    	Analyze_Compared_Difficulties.generateDifficultyComparisonPlots();
    	System.out.println("done.");
    }
}
