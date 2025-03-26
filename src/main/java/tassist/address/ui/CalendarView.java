package tassist.address.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import tassist.address.logic.Logic;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.TimedEvent;

/**
 * A UI component that displays timed events in a calendar format.
 */
public class CalendarView extends UiPart<Region> {
    private static final String FXML = "CalendarView.fxml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML
    private HBox calendarGrid;

    private final Logic logic;

    /**
     * Constructor for CalendarView.
     * @param events The list of timed events to display.
     * @param logic The logic component.
     */
    public CalendarView(List<TimedEvent> events, Logic logic) {
        super(FXML);
        this.logic = logic;
        updateEvents(events);
    }

    /**
     * Updates the calendar view with the given list of timed events.
     */
    public void updateEvents(List<TimedEvent> events) {
        calendarGrid.getChildren().clear();

        // Sort events by date
        List<TimedEvent> sortedEvents = events.stream()
                .sorted((e1, e2) -> e1.getTime().compareTo(e2.getTime()))
                .collect(Collectors.toList());

        // Group events by date using a TreeMap with custom comparator for chronological order
        Map<LocalDateTime, List<TimedEvent>> eventsByDate = new TreeMap<>((d1, d2) -> d1.compareTo(d2));

        for (TimedEvent event : sortedEvents) {
            LocalDateTime eventDate = event.getTime().toLocalDate().atStartOfDay();
            eventsByDate.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
        }

        // Create calendar grid
        for (Map.Entry<LocalDateTime, List<TimedEvent>> entry : eventsByDate.entrySet()) {
            String date = entry.getKey().format(DATE_FORMATTER);
            List<TimedEvent> dayEvents = entry.getValue();

            // Sort events within the day by time
            dayEvents.sort((e1, e2) -> e1.getTime().compareTo(e2.getTime()));

            // Create a column for this date
            VBox dateColumn = new VBox(10);
            dateColumn.getStyleClass().add("calendar-date-column");
            dateColumn.setMinWidth(250); // Set minimum width for each date column

            // Date header
            Label dateLabel = new Label(date);
            dateLabel.getStyleClass().add("calendar-date");
            dateColumn.getChildren().add(dateLabel);

            // Events for the day
            VBox dayEventsBox = new VBox(8);
            dayEventsBox.getStyleClass().add("calendar-events");
            for (TimedEvent event : dayEvents) {
                VBox eventBox = new VBox(2);
                eventBox.getStyleClass().add("calendar-event");

                // Event name and type
                TextFlow nameFlow = new TextFlow();
                Text nameText = new Text(event.getName());
                nameText.getStyleClass().add("event-name");
                Text typeText = new Text(" - " + event.getClass().getSimpleName().toLowerCase());
                typeText.getStyleClass().add("event-type");
                nameFlow.getChildren().addAll(nameText, typeText);
                eventBox.getChildren().add(nameFlow);

                // Description
                if (!event.getDescription().isEmpty()) {
                    Text descriptionText = new Text(event.getDescription());
                    descriptionText.getStyleClass().add("event-description");
                    eventBox.getChildren().add(descriptionText);
                }

                // Assigned persons
                List<Person> assignedPersons = getAssignedPersons(event);
                if (!assignedPersons.isEmpty()) {
                    VBox assignedBox = new VBox(1);
                    assignedBox.getStyleClass().add("assigned-persons");
                    Text assignedLabel = new Text("Assigned to:");
                    assignedLabel.getStyleClass().add("assigned-label");
                    assignedBox.getChildren().add(assignedLabel);

                    for (Person person : assignedPersons) {
                        Text personText = new Text("• " + person.getName().toString());
                        personText.getStyleClass().add("assigned-person");
                        assignedBox.getChildren().add(personText);
                    }
                    eventBox.getChildren().add(assignedBox);
                }

                dayEventsBox.getChildren().add(eventBox);
            }
            dateColumn.getChildren().add(dayEventsBox);
            calendarGrid.getChildren().add(dateColumn);
        }
    }

    /**
     * Gets the list of persons assigned to a timed event.
     */
    private List<Person> getAssignedPersons(TimedEvent event) {
        return logic.getFilteredPersonList().stream()
                .filter(person -> person.hasTimedEvent(event))
                .collect(Collectors.toList());
    }
}
