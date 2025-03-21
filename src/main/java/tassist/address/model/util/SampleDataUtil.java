package tassist.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import tassist.address.model.AddressBook;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.person.Address;
import tassist.address.model.person.Email;
import tassist.address.model.person.Github;
import tassist.address.model.person.Name;
import tassist.address.model.person.Person;
import tassist.address.model.person.Phone;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;
import tassist.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    public static final Github DEFAULT_GITHUB = new Github("https://github.com/default");

    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"), new StudentId("A0000000B"), DEFAULT_GITHUB,
                getTagSet("friends"), new Progress("20")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"), new StudentId("A0000000B"), DEFAULT_GITHUB,
                getTagSet("colleagues", "friends"), new Progress("35")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"), new StudentId("A0000000B"), DEFAULT_GITHUB,
                getTagSet("neighbours"), new Progress("80")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"), new StudentId("A0000000B"), DEFAULT_GITHUB,
                getTagSet("family"), new Progress("15")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"), new StudentId("A0000000B"), DEFAULT_GITHUB,
                getTagSet("classmates"), new Progress("60")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"), new StudentId("A0000000B"), DEFAULT_GITHUB,
                getTagSet("colleagues"), new Progress("77"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
