package tassist.address.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CsvJsonConverter {

    public void convertCsvToJson(String csvFile, String jsonFile) throws FileNotFoundException,
            IOException, CsvException {
        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);

        List<String[]> rows = csvReader.readAll();

        String[] headers = rows.get(0);
        List<Map<String, Object>> data = retrieveData(headers, rows);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Map<String, Object>>> wrappedData = new HashMap<>();
        wrappedData.put("persons", data);
        objectMapper.writeValue(new File(jsonFile), wrappedData);
        System.out.println("CSV has been successfully converted to JSON!");
    }

    private List<Map<String, Object>> retrieveData(String[] headers, List<String[]> rows) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Map<String, Object> personData = new HashMap<>();

            for (int j = 0; j < headers.length; j++) {
                String key = headers[j];
                String value = row[j];

                if (key.equals("tags")) {
                    personData.put(key, Arrays.asList(value.split(",")));
                } else {
                    personData.put(key, value);
                }
            }

            data.add(personData);
        }

        return data;
    }
}
