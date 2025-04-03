package tassist.address.model.timedevents;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TimedEventTest {
    @Test
    public void getAttributeTest() {
        List<String> attributes = TimedEvent.getAttributes();
        List<String> expectedAttributes = new ArrayList<>();
        expectedAttributes.add("name");
        expectedAttributes.add("description");
        expectedAttributes.add("time");
        expectedAttributes.add("type");
        assertEquals(attributes, expectedAttributes);
    }
}
