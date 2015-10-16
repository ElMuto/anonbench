package org.deidentifier.arx.analysis;

import java.io.IOException;
import java.text.ParseException;

public class GenerateAllPlots {
    public static void main(String[] args) throws IOException, ParseException {
    	Analyze_IOD.generateDifficultyInfluencePlots("Plots_influenceOnDifficulty.pdf" , true);
    	Compare_Difficulties.generateDifficultyComparisonPlots();
    	System.out.println("done.");
    }
}
