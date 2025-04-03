package tassist.address.testutil;

import static tassist.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_PROGRESS_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_PROGRESS_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static tassist.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tassist.address.model.AddressBook;
import tassist.address.model.person.Person;

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 */
public class TypicalPersons {

    public static final String DEFAULT_REPOSITORY = "No Repository";

    public static final Person ALICE = new PersonBuilder()
            .withName("Alice Pauline")
            .withEmail("alice@u.nus.edu")
            .withPhone("94351253")
            .withClassNumber("T01")
            .withGithub("https://github.com/alice")
            .withStudentId("A1111111B")
            .withProjectTeam("WealthAssist")
            .withRepository(DEFAULT_REPOSITORY)
            .withTags("friends")
            .withProgress("0")
            .build();
    public static final Person BENSON = new PersonBuilder()
            .withName("Benson Meier")
            .withEmail("johnd@u.nus.edu")
            .withPhone("98765432")
            .withStudentId("A0101011A")
            .withClassNumber("T01")
            .withGithub("https://github.com/benson")
            .withProjectTeam("WealthAssist")
            .withRepository(DEFAULT_REPOSITORY)
            .withTags("owesMoney", "friends")
            .withProgress("30").build();
    public static final Person CARL = new PersonBuilder()
            .withName("Carl Kurz")
            .withPhone("95352563")
            .withEmail("heinz@u.nus.edu")
            .withStudentId("A0101010C")
            .withClassNumber("No tutorial assigned")
            .withGithub("https://github.com/carl")
            .withProjectTeam("WealthAssist")
            .withRepository(DEFAULT_REPOSITORY)
            .withProgress("50")
            .build();
    public static final Person DANIEL = new PersonBuilder()
            .withName("Daniel Meier")
            .withPhone("87652533")
            .withEmail("cornelia@u.nus.edu")
            .withStudentId("A0001234X")
            .withGithub("https://github.com/daniel")
            .withProjectTeam("Bim Bim Bap")
            .withRepository(DEFAULT_REPOSITORY)
            .withTags("friends")
            .withClassNumber("T04")
            .withProgress("0")
            .build();
    public static final Person ELLE = new PersonBuilder()
            .withName("Elle Meyer").withPhone("9482224")
            .withEmail("werner@u.nus.edu")
            .withStudentId("A1928374M")
            .withGithub("https://github.com/elle")
            .withProjectTeam("Bim Bim Bap")
            .withRepository(DEFAULT_REPOSITORY)
            .withClassNumber("No tutorial assigned")
            .withProgress("60")
            .build();
    public static final Person FIONA = new PersonBuilder()
            .withName("Fiona Kunz")
            .withPhone("9482427")
            .withEmail("lydia@u.nus.edu")
            .withGithub("https://github.com/fiona")
            .withStudentId("A7654321J")
            .withProjectTeam("Bim Bim Bap")
            .withRepository(DEFAULT_REPOSITORY)
            .withClassNumber("No tutorial assigned")
            .withProgress("40")
            .build();
    public static final Person GEORGE = new PersonBuilder()
            .withName("George Best")
            .withPhone("9482442")
            .withEmail("anna@u.nus.edu")
            .withStudentId("A9182736L")
            .withGithub("https://github.com/george")
            .withProjectTeam("Bim Bim Bap")
            .withRepository(DEFAULT_REPOSITORY)
            .withClassNumber("No tutorial assigned")
            .withProgress("5").build();

    // Manually added
    public static final Person HOON = new PersonBuilder()
            .withName("Hoon Meier")
            .withPhone("8482424")
            .withEmail("stefan@u.nus.edu")
            .withGithub("https://github.com/hoon")
            .withStudentId("A2468135Z")
            .withClassNumber("No tutorial assigned")
            .withProgress("20").build();
    public static final Person IDA = new PersonBuilder()
            .withName("Ida Mueller")
            .withPhone("8482131")
            .withEmail("hans@u.nus.edu")
            .withGithub("https://github.com/ida")
            .withStudentId("A1357924Q")
            .withClassNumber("No tutorial assigned")
            .withProgress("90").build();

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final Person AMY = new PersonBuilder()
            .withName(VALID_NAME_AMY)
            .withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY)
            .withStudentId(VALID_STUDENTID_AMY)
            .withRepository(DEFAULT_REPOSITORY)
            .withClassNumber("T01")
            .withTags(VALID_TAG_FRIEND)
            .withProgress(VALID_PROGRESS_AMY)
            .build();
    public static final Person BOB = new PersonBuilder()
            .withName(VALID_NAME_BOB)
            .withPhone(VALID_PHONE_BOB)
            .withEmail(VALID_EMAIL_BOB)
            .withStudentId(VALID_STUDENTID_BOB)
            .withRepository(DEFAULT_REPOSITORY)
            .withClassNumber("T15")
            .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .withProgress(VALID_PROGRESS_BOB)
            .build();


    private TypicalPersons() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        return ab;
    }

    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
