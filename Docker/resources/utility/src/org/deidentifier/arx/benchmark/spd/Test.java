package org.deidentifier.arx.benchmark.spd;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws ParseException, IOException {
        
        String fileInput = "C:\\Users\\prasser\\Downloads\\SPD_Science\\SPD_dataset.txt";
        String fileCrises = "C:\\Users\\prasser\\Downloads\\SPD_Science\\5_anonymous_SPD.txt";
        String fileARXLocal = "C:\\Users\\prasser\\Downloads\\SPD_Science\\5_anonymous_SPD_arx_local_loss.csv";
        String fileARXGlobal = "C:\\Users\\prasser\\Downloads\\SPD_Science\\5_anonymous_SPD_arx_global_loss.csv";
        String fileARXGlobalHeuristic = "C:\\Users\\prasser\\Downloads\\SPD_Science\\5_anonymous_SPD_arx_global_heuristic_loss.csv";
        
        String[] attributes = new String[]{"oshpd_id",
                                           "age_yrs",
                                           "sex",
                                           "ethncty",
                                           "race",
                                           "patzip",
                                           "patcnty",
                                           "los",
                                           "adm_qtr"};
        
        List<String[]> dataInput = SoriaComasIO.loadData(fileInput, attributes);
        List<String[]> dataCrises = SoriaComasIO.loadData(fileCrises, attributes);
        List<String[]> dataARXLocal = SoriaComasIO.loadData(fileARXLocal, attributes);
        List<String[]> dataARXGlobal = SoriaComasIO.loadData(fileARXGlobal, attributes);
        List<String[]> dataARXGlobalHeuristic = SoriaComasIO.loadData(fileARXGlobalHeuristic, attributes);
        
        System.out.println("Normalized euclidean distance [0, 1]:");
        System.out.println(" - Crises: " + SoriaComasFunctions.calculateIL(dataInput, dataCrises));
        System.out.println(" - ARX (global): " + SoriaComasFunctions.calculateIL(dataInput, dataARXGlobal));
        System.out.println(" - ARX (global-heuristic): " + SoriaComasFunctions.calculateIL(dataInput, dataARXGlobalHeuristic));
        System.out.println(" - ARX (local): " + SoriaComasFunctions.calculateIL(dataInput, dataARXLocal));
        System.out.println("Relative average class size [0, 1]:");
        System.out.println(" - Crises: " + SoriaComasFunctions.calculateAverageClassSize(dataInput, dataCrises));
        System.out.println(" - ARX (global): " + SoriaComasFunctions.calculateAverageClassSize(dataInput, dataARXGlobal));
        System.out.println(" - ARX (global-heuristic): " + SoriaComasFunctions.calculateAverageClassSize(dataInput, dataARXGlobalHeuristic));
        System.out.println(" - ARX (local): " + SoriaComasFunctions.calculateAverageClassSize(dataInput, dataARXLocal));
        System.out.println("Normalized granularity (loss) [0, 1]:");
        System.out.println(" - Crises: " + SoriaComasFunctions.calculateGranularity(dataInput, dataCrises));
        System.out.println(" - ARX (global): " + SoriaComasFunctions.calculateGranularity(dataInput, dataARXGlobal));
        System.out.println(" - ARX (global-heuristic): " + SoriaComasFunctions.calculateGranularity(dataInput, dataARXGlobalHeuristic));
        System.out.println(" - ARX (local): " + SoriaComasFunctions.calculateGranularity(dataInput, dataARXLocal));
    }
}
