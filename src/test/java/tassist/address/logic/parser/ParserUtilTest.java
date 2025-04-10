package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static tassist.address.testutil.Assert.assertThrows;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Email;
import tassist.address.model.person.Name;
import tassist.address.model.person.Phone;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;
import tassist.address.model.tag.Tag;

public class ParserUtilTest {

    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_CLASS = "T00";
    private static final String INVALID_STUDENTID = "B0000000M";
    private static final String INVALID_PROGRESS = "180";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_EMAIL = "rachel@u.nus.edu";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_CLASS_1 = "T01";
    private static final String VALID_CLASS_2 = "R03";
    private static final String VALID_CLASS_3 = "L04";
    private static final String VALID_CLASS_4 = "L15C";
    private static final String VALID_STUDENTID = "A0000000M";
    private static final String VALID_PROGRESS_1 = "80";
    private static final String VALID_PROGRESS_2 = "20%";

    private static final String WHITESPACE = " \t\r\n";

    private static final String FILE_PATH_1 = "sample-1.csv";
    private static final String FILE_PATH_2 = "sample-2.csv";
    private static final String FILE_PATH_3 = "sample-3.csv";

    @TempDir
    public Path testRoot;

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseClassNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseClassNumber((String) null));
    }

    @Test
    public void parseClassNumber_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseClassNumber(INVALID_CLASS));
    }

    @Test
    public void parseClassNumber_validValueStartingWithT_returnsClassNumber() throws Exception {
        ClassNumber expectedClassNumber = new ClassNumber(VALID_CLASS_1);
        assertEquals(expectedClassNumber, ParserUtil.parseClassNumber(VALID_CLASS_1));
    }

    @Test
    public void parseClassNumber_validValueStartingWithR_returnsClassNumber() throws Exception {
        ClassNumber expectedClassNumber = new ClassNumber(VALID_CLASS_2);
        assertEquals(expectedClassNumber, ParserUtil.parseClassNumber(VALID_CLASS_2));
    }

    @Test
    public void parseClassNumber_validValueStartingWithL_returnsClassNumber() throws Exception {
        ClassNumber expectedClassNumber = new ClassNumber(VALID_CLASS_3);
        assertEquals(expectedClassNumber, ParserUtil.parseClassNumber(VALID_CLASS_3));
    }

    @Test
    public void parseClassNumber_validValueStartingWithLWithSuffix_returnsClassNumber() throws Exception {
        ClassNumber expectedClassNumber = new ClassNumber(VALID_CLASS_4);
        assertEquals(expectedClassNumber, ParserUtil.parseClassNumber(VALID_CLASS_4));
    }

    @Test
    public void parseClassNumber_validValueWithWhitespace_returnsTrimmedClassNumber() throws Exception {
        String classNumberWithWhitespace = WHITESPACE + VALID_CLASS_1 + WHITESPACE;
        ClassNumber expectedClassNumber = new ClassNumber(VALID_CLASS_1);
        assertEquals(expectedClassNumber, ParserUtil.parseClassNumber(classNumberWithWhitespace));
    }

    @Test
    public void parseStudentId_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudentId((String) null));
    }

    @Test
    public void parseStudentId_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseStudentId(INVALID_STUDENTID));
    }

    @Test
    public void parseStudentId_validValueWithoutWhitespace_returnsStudentId() throws Exception {
        StudentId expectedStudentId = new StudentId(VALID_STUDENTID);
        assertEquals(expectedStudentId, ParserUtil.parseStudentId(VALID_STUDENTID));
    }

    @Test
    public void parseStudentId_validValueWithWhitespace_returnsTrimmedStudentId() throws Exception {
        String studentIdWithWhitespace = WHITESPACE + VALID_STUDENTID + WHITESPACE;
        StudentId expectedStudentId = new StudentId(VALID_STUDENTID);
        assertEquals(expectedStudentId, ParserUtil.parseStudentId(studentIdWithWhitespace));
    }

    @Test
    public void parseProgress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseProgress(null));
    }

    @Test
    public void parseProgress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseProgress(INVALID_PROGRESS));
    }

    @Test
    public void parseProgress_validValueWithPercentage_returnsProgress() throws Exception {
        assertEquals(new Progress("20"), ParserUtil.parseProgress(VALID_PROGRESS_2));
    }

    @Test
    public void parseProgress_validValueWithWhiteSpace_returnsProgress() throws Exception {
        String progressWithWhiteSpace = WHITESPACE + VALID_PROGRESS_1 + WHITESPACE;
        Progress expectedProgress = new Progress(VALID_PROGRESS_1);
        assertEquals(expectedProgress, ParserUtil.parseProgress(progressWithWhiteSpace));
    }

    @Test
    public void parseProgress_validValueWithoutWhitespace_returnsProgress() throws Exception {
        String progressWithoutWhiteSpace = VALID_PROGRESS_1;
        Progress expectedProgress = new Progress(VALID_PROGRESS_1);
        assertEquals(expectedProgress, ParserUtil.parseProgress(progressWithoutWhiteSpace));
    }

    @Test
    public void parseFilePath_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseFilePath(null));
    }

    @Test
    public void parseFilePath_absolutePath_returnsSamePath() throws Exception {
        // mimics absolute path
        final Path absoluteFilePath = testRoot.resolve(FILE_PATH_1);

        // creates the file so it "exists"
        if (!Files.exists(absoluteFilePath)) {
            Files.createFile(absoluteFilePath);
        }

        Path expectedAbsolutePath = absoluteFilePath.toAbsolutePath();
        assertEquals(expectedAbsolutePath, ParserUtil.parseFilePath(absoluteFilePath.toString()));
    }

    @Test
    public void parseFilePath_absolutePathWithWhitespace_returnsTrimmedAbsolutePath() throws Exception {
        // mimics absolute path
        final Path absoluteFilePath = testRoot.resolve(FILE_PATH_2);

        // creates the file so it "exists"
        if (!Files.exists(absoluteFilePath)) {
            Files.createFile(absoluteFilePath);
        }

        String absolutePathWithWhitespaceString = WHITESPACE + absoluteFilePath.toString() + WHITESPACE;
        Path expectedAbsolutePath = absoluteFilePath.toAbsolutePath();
        assertEquals(expectedAbsolutePath, ParserUtil.parseFilePath(absolutePathWithWhitespaceString));
    }

    @Test
    public void parseFilePath_relativePath_throwsParseException() {
        // VALID_FILE_PATH is relative
        assertThrows(ParseException.class, () -> ParserUtil.parseFilePath(FILE_PATH_1));
    }

    @Test
    public void parseFilePath_rootDirectory_throwsParseException() {
        // cannot be root directory
        assertThrows(ParseException.class, () -> ParserUtil.parseFilePath("/"));
    }

    @Test
    public void parseFilePath_invalidCharacters_throwsParseException() {
        // Test various invalid characters in file paths
        String[] invalidPaths = {
            "C:/test|file.csv", // pipe character
            "C:/test?file.csv", // question mark
            "C:/test*file.csv", // asterisk
            "C:/test<file.csv", // less than
            "C:/test>file.csv", // greater than
            "C:/test\"file.csv", // double quote
            "C:/test:file.csv" // colon
        };

        for (String invalidPath : invalidPaths) {
            assertThrows(ParseException.class, () -> ParserUtil.parseFilePath(invalidPath));
        }
    }

    @Test
    public void parseFilePath_validPathSeparators_success() throws Exception {
        // Test valid path separators
        String[] validPaths = {
            testRoot.resolve("test/file.csv").toString(), // Forward slash
            testRoot.resolve("test\\file.csv").toString(), // Windows backslash
            testRoot.resolve("test/sub/file.csv").toString(), // Multiple forward slashes
            testRoot.resolve("test\\sub\\file.csv").toString() // Multiple backslashes
        };

        for (String validPath : validPaths) {
            Path parsedPath = ParserUtil.parseFilePath(validPath);
            assertTrue(parsedPath.isAbsolute());
            assertTrue(parsedPath.toString().contains("test"));
            assertTrue(parsedPath.toString().endsWith("file.csv"));
        }
    }

    @Test
    public void parseFilePath_invalidPathFormat_throwsParseException() {
        // Test relative paths (not absolute)
        String[] relativePaths = {
            "test/file.csv", // Simple relative path
            "./test/file.csv", // Current directory
            "../test/file.csv", // Parent directory
            "folder/sub/file.csv" // Multiple levels
        };

        for (String relativePath : relativePaths) {
            assertThrows(ParseException.class, () -> ParserUtil.parseFilePath(relativePath));
        }

        // Test paths with zero name count (root paths)
        String[] rootPaths = {
            "/", // Unix root
            "C:/", // Windows root
            "D:\\" // Windows root alternative
        };

        for (String rootPath : rootPaths) {
            assertThrows(ParseException.class, () -> ParserUtil.parseFilePath(rootPath));
        }
    }

    @Test
    public void parseFilePath_nullCharacterInPath_throwsParseException() {
        String pathWithNullChar = "C:/test\0file.csv"; // null character in path
        assertThrows(ParseException.class, () -> ParserUtil.parseFilePath(pathWithNullChar));
    }
}
