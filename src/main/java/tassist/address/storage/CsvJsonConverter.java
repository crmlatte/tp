package tassist.address.storage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

/**
 * A utility class that handles the conversion between CSV and JSON formats.
 */
public class CsvJsonConverter {

    /**
     * Converts a CSV file to a JSON file.
     * <p>
     * This method reads a CSV file, processes its content, and converts it into a JSON format, which is then
     * saved to the provided JSON file path. The JSON is formatted with indentation for better readability.
     * </p>
     *
     * @param csvFilePath the path to the input CSV file to be converted
     * @param jsonFilePath the path to the output JSON file where the converted data will be saved
     * @throws IOException if there is an error reading the CSV file or writing the JSON file
     * @throws CsvException if there is an error during CSV parsing
     */
    public void convertCsvToJson(Path csvFilePath, Path jsonFilePath) throws IOException, CsvException {
        FileReader fileReader = new FileReader(csvFilePath.toString());
        CSVReader csvReader = new CSVReader(fileReader);

        List<String[]> rows = csvReader.readAll();

        if (rows.isEmpty()) {
            throw new CsvException("No data");
        }

        List<String[]> personRows = new ArrayList<>();
        List<String[]> timedEventRows = new ArrayList<>();

        boolean isTimedEventSection = false;

        for (String[] row : rows) {
            if (row.length > 0 && row[0].equalsIgnoreCase("timedEvents")) {
                isTimedEventSection = true; // We reached the timedEvents section
            } else {
                if (isTimedEventSection) {
                    timedEventRows.add(row);
                } else {
                    personRows.add(row);
                }
            }
        }

        String[] personHeaders = personRows.get(0);
        List<Map<String, Object>> personsData = retrieveData(personHeaders, personRows);

        String[] timedEventHeaders = timedEventRows.get(0);
        List<Map<String, Object>> timedEventsData = retrieveData(timedEventHeaders, timedEventRows);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, List<Map<String, Object>>> wrappedData = new LinkedHashMap<>();
        wrappedData.put("persons", personsData);
        wrappedData.put("timedEvents", timedEventsData);
        objectMapper.writeValue(new File(jsonFilePath.toString()), wrappedData);

        System.out.println("CSV has been successfully converted to JSON!");
    }

    private List<Map<String, Object>> retrieveData(String[] headers, List<String[]> rows) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Map<String, Object> personData = new LinkedHashMap<>();

            for (int j = 0; j < headers.length; j++) {
                String key = headers[j];
                String value = row[j];

                if (key.equalsIgnoreCase("tags")) {
                    Object newValue = processTags(value);
                    personData.put(key, newValue);
                } else if (key.equalsIgnoreCase("timedEvents")) {
                    List<Map<String, String>> timedEventsList = processPersonTimedEvents(value);
                    personData.put(key, timedEventsList);
                } else {
                    String newValue = processAttribute(value);
                    personData.put(key, newValue);
                }
            }

            data.add(personData);
        }

        return data;
    }

    private String processAttribute(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        } else {
            return value;
        }
    }

    private Object processTags(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new String[0];
        } else {
            return Arrays.asList(value.split(","));
        }
    }

    private List<Map<String, String>> processPersonTimedEvents(String value) {
        List<Map<String, String>> timedEventsList = new ArrayList<>();
        String[] splitted = value.split(",");

        for (int i = 0; i < splitted.length / 4; i++) {
            Map<String, String> timedEvent = new LinkedHashMap<>();
            timedEvent.put("name", splitted[(i * 4)].trim());
            timedEvent.put("description", splitted[(i * 4) + 1].trim());
            timedEvent.put("time", splitted[(i * 4) + 2].trim());
            timedEvent.put("type", splitted[(i * 4) + 3].trim());

            timedEventsList.add(timedEvent);
        }

        return timedEventsList;
    }
}
