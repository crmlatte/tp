package tassist.address.storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import javafx.collections.ObservableList;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.person.Person;
import tassist.address.model.tag.Tag;
import tassist.address.model.timedevents.TimedEvent;

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

        csvReader.close();
        fileReader.close();
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

    /**
     * Converts the data from a {@link ReadOnlyAddressBook} into a CSV file format.
     * <p>
     * This method writes the person and timed event information from the given {@code addressBook}
     * into the specified {@code csvFilePath}. It first writes the headers for persons and timed events,
     * followed by their respective data. It ensures that all person-related data (like name, email, tags, etc.)
     * and timed event-related data (like name, description, time, etc.) are properly formatted in the CSV.
     * </p>
     *
     * @param csvFilePath The path where the CSV file should be saved. This path must be valid and writable.
     * @param addressBook The address book containing persons and timed events that will be converted to CSV.
     * @throws IOException If an error occurs during file writing, such as file access issues or invalid paths.
     */
    public void convertJsonToCsv(Path csvFilePath, ReadOnlyAddressBook addressBook) throws IOException {
        FileWriter fileWriter = new FileWriter(csvFilePath.toString());
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        ObservableList<Person> persons = addressBook.getPersonList();
        ObservableList<TimedEvent> timedEvents = addressBook.getTimedEventList();

        writePersons(csvWriter, persons);

        csvWriter.writeNext(new String[]{"timedEvents"});

        writeTimedEvents(csvWriter, timedEvents);

        csvWriter.close();
        fileWriter.close();
    }

    private void writePersons(CSVWriter csvWriter, ObservableList<Person> persons) {
        String[] personHeader = Person.getAttributes().toArray(new String[0]);
        csvWriter.writeNext(personHeader);

        for (Person person : persons) {
            String[] personData = {
                    person.getName().toString(),
                    person.getPhone().toString(),
                    person.getEmail().toString(),
                    person.getClassNumber().toString(),
                    person.getStudentId().toString(),
                    person.getGithub().toString(),
                    person.getProjectTeam().toString(),
                    convertTagsToCsvString(person.getTags()),
                    person.getProgress().toString(),
                    convertTimedEventsToCsvString(person.getTimedEvents())
            };

            csvWriter.writeNext(personData);
        }
    }

    private void writeTimedEvents(CSVWriter csvWriter, ObservableList<TimedEvent> timedEvents) {
        String[] timedEventHeader = TimedEvent.getAttributes().toArray(new String[0]);
        csvWriter.writeNext(timedEventHeader);

        for (TimedEvent timedEvent : timedEvents) {
            String[] timedEventData = {
                    timedEvent.getName(),
                    timedEvent.getDescription(),
                    timedEvent.getTime().toString(),
                    timedEvent.getClass().getSimpleName()
            };

            csvWriter.writeNext(timedEventData);
        }
    }

    private String convertTagsToCsvString(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }

        StringBuilder tagsData = new StringBuilder();
        for (Tag tag : tags) {
            tagsData.append(tag.toString().replaceAll("[\\[\\]]", ""))
                    .append(",");
        }

        if (tagsData.length() > 0) {
            tagsData.setLength(tagsData.length() - 1); // Remove the last comma
        }

        return tagsData.toString();
    }

    private String convertTimedEventsToCsvString(ObservableList<TimedEvent> timedEvents) {
        if (timedEvents.isEmpty()) {
            return "";
        }

        StringBuilder timedEventData = new StringBuilder();
        for (TimedEvent timedEvent : timedEvents) {
            timedEventData.append(timedEvent.getName())
                    .append(",")
                    .append(timedEvent.getDescription())
                    .append(",")
                    .append(timedEvent.getTime())
                    .append(",")
                    .append(timedEvent.getClass().getSimpleName())
                    .append(",");
        };

        if (timedEventData.length() > 0) {
            timedEventData.setLength(timedEventData.length() - 1); // Remove the last comma
        }

        return timedEventData.toString();
    }
}
