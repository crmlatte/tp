package tassist.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

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
import com.opencsv.exceptions.CsvException;

public class CsvJsonConverterTest {

    private CsvJsonConverter csvJsonConverter;

    @TempDir
    public Path testFolder;

    private static final Path TEST_DATA_FOLDER = Paths.get("src","test", "data", "CsvJsonConverterTest");

    @BeforeEach
    public void setUp() {
        csvJsonConverter = new CsvJsonConverter();
    }

    @Test
    public void testConvertCsvToJson_validCsv_createsJson(@TempDir Path tempDir) throws IOException, CsvException {
        Path inputCsv = TEST_DATA_FOLDER.resolve("valid.csv");
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
    public void testConvertCsvToJson_nonExistentCsv_throwsIOException() {
        Path inputCsv = TEST_DATA_FOLDER.resolve("non-existent.csv");
        Path outputJson = testFolder.resolve("TempOutput.json");
        assertThrows(IOException.class, () -> csvJsonConverter.convertCsvToJson(inputCsv, outputJson));
    }
}
