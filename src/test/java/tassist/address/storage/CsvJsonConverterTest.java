package tassist.address.storage;

/*import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import tassist.address.commons.exceptions.DataLoadingException;
import tassist.address.logic.commands.ImportCommandTest;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.ReadOnlyAddressBook;
*/

public class CsvJsonConverterTest {

    /*private static final Path TEST_DATA_FOLDER =
            Paths.get("src", "test", "data", "CsvJsonConverterTest");

    @TempDir
    public Path testFolder;

    private CsvJsonConverter csvJsonConverter;
    private Model model;

    @BeforeEach
    public void setUp() throws DataLoadingException {
        csvJsonConverter = new CsvJsonConverter();
        Path addressBookFilePath = TEST_DATA_FOLDER.resolve("testConverterFromJson.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFilePath);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(testFolder.resolve("userPrefs.json"));
        Storage storage = new ImportCommandTest.TestStorageManager(addressBookStorage, userPrefsStorage);
        ReadOnlyAddressBook addressBook = storage.readAddressBook().get();
        model = new ModelManager(addressBook, new ImportCommandTest.TestUserPrefs(addressBookFilePath));
    }

    @Test
    public void testConvertCsvToJson_validCsv_createsJson(@TempDir Path tempDir) throws IOException, CsvException {
        Path inputCsv = TEST_DATA_FOLDER.resolve("testConverterFromJson.csv");
        Path outputJson = tempDir.resolve("TempOutput.json");

        csvJsonConverter.convertCsvToJson(inputCsv, outputJson);

        assertTrue(Files.exists(outputJson), "The JSON file should be created");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Map<String, Object>>> jsonData = objectMapper.readValue(outputJson.toFile(), Map.class);

        List<Map<String, Object>> personsData = jsonData.get("persons");
        assertNotNull(personsData);
        assertEquals(3, personsData.size(), "The number of persons should be 3");

        List<Map<String, Object>> timedEventsData = jsonData.get("timedEvents");
        assertNotNull(timedEventsData);
        assertEquals(1, timedEventsData.size(), "There should be 1 global timed event");

        List<Map<String, String>> timedEventsForAlex =
                (List<Map<String, String>>) personsData.get(0).get("timedEvents");
        assertNotNull(timedEventsForAlex);
        assertEquals(1, timedEventsForAlex.size(), "There should be 1 timed event for Alex");
    }

    @Test
    public void testConvertCsvToJson_emptyCsv_throwsCsvException() {
        Path inputCsv = TEST_DATA_FOLDER.resolve("empty.csv");
        Path outputJson = testFolder.resolve("TempOutput.json");
        assertThrows(CsvException.class, () -> csvJsonConverter.convertCsvToJson(inputCsv, outputJson));
    }

    @Test
    public void testConvertCsvToJson_nonExistentCsv_throwsIoException() {
        Path inputCsv = TEST_DATA_FOLDER.resolve("non-existent.csv");
        Path outputJson = testFolder.resolve("TempOutput.json");
        assertThrows(IOException.class, () -> csvJsonConverter.convertCsvToJson(inputCsv, outputJson));
    }

    @Test
    public void testConvertJsonToCsv_validJson_createsCsv() throws IOException, CsvException {
        ReadOnlyAddressBook addressBook = model.getAddressBook();
        Path outputCsv = testFolder.resolve("TestOutput.csv");

        csvJsonConverter.convertJsonToCsv(outputCsv, addressBook);

        assertTrue(Files.exists(outputCsv), "The CSV file should be created");

        FileReader fileReader = new FileReader(outputCsv.toString());
        CSVReader csvReader = new CSVReader(fileReader);

        List<String[]> csvContent = csvReader.readAll().stream().toList();

        // Verify the CSV personHeader
        String[] personHeader = csvContent.get(0);
        assertEquals("name", personHeader[0]);
        assertEquals("phone", personHeader[1]);
        assertEquals("email", personHeader[2]);
        assertEquals("classNumber", personHeader[3]);
        assertEquals("studentId", personHeader[4]);
        assertEquals("github", personHeader[5]);
        assertEquals("projectTeam", personHeader[6]);
        assertEquals("tags", personHeader[7]);
        assertEquals("progress", personHeader[8]);
        assertEquals("timedEvents", personHeader[9]);

        // Verify that data is written correctly
        String[] firstPerson = csvContent.get(1);
        assertEquals("Alex Yeoh", firstPerson[0]);
        assertEquals("87438807", firstPerson[1]);
        assertEquals("alexyeoh@example.com", firstPerson[2]);
        assertEquals("T01", firstPerson[3]);
        assertEquals("A0000001B", firstPerson[4]);
        assertEquals("https://github.com/default", firstPerson[5]);
        assertEquals("WealthVault", firstPerson[6]);
        assertEquals("friends", firstPerson[7]);
        assertEquals("20%", firstPerson[8]);
        assertEquals("cs2103t,,2025-04-30T23:59,Assignment", firstPerson[9]);

        String[] timedEventsHeader = csvContent.get(5);
        assertEquals("name", timedEventsHeader[0]);
        assertEquals("description", timedEventsHeader[1]);
        assertEquals("time", timedEventsHeader[2]);
        assertEquals("type", timedEventsHeader[3]);

        String[] firstTimedEvent = csvContent.get(6);
        assertEquals("new", firstTimedEvent[0]);
        assertEquals("", firstTimedEvent[1]);
        assertEquals("2066-01-30T23:59", firstTimedEvent[2]);
        assertEquals("Assignment", firstTimedEvent[3]);

        csvReader.close();
        fileReader.close();
    }*/
}