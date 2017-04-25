package org.deidentifier.arx.benchmark.spd;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataSource;

public class SoriaComasIO {

    public static List<String[]> loadData(String file, String[] attributes) throws IOException {
        
        DataSource source = DataSource.createCSVSource(file, Charset.forName("UTF-8"), ',', true);
        for (String attribute : attributes) {
            source.addColumn(attribute);
        }
        Data data = Data.create(source);
        
        List<String[]> result = new ArrayList<>();
        Iterator<String[]> iter = data.getHandle().iterator();
        iter.next(); // Skip header
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    public static List<String[]> createSuppressedDataset(int records, int columns) {
        String[] record = new String[columns];
        Arrays.fill(record, "*");
        List<String[]> result = new ArrayList<>();
        for (int i=0; i<records; i++) {
            result.add(record);
        }
        return result;
    }
}
