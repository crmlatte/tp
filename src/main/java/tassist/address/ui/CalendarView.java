package tassist.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import tassist.address.model.timedevents.TimedEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A UI component that displays timed events in a calendar format.
 */
public class CalendarView extends UiPart<Region> {
    private static final String FXML = "CalendarView.fxml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private GridPane calendarGrid;

    @FXML
    private VBox eventList;

    public CalendarView(List<TimedEvent> events) {
        super(FXML);
        updateEvents(events);
    }

    /**
     * Updates the calendar view with the given list of timed events.
     */
    public void updateEvents(List<TimedEvent> events) {
        calendarGrid.getChildren().clear();
        eventList.getChildren().clear();

        // Sort events by date
        List<TimedEvent> sortedEvents = events.stream()
                .sorted((e1, e2) -> e1.getTime().compareTo(e2.getTime()))
                .collect(Collectors.toList());

        // Group events by date using a TreeMap with custom comparator for reverse chronological order
        Map<LocalDateTime, List<TimedEvent>> eventsByDate = new TreeMap<>((d1, d2) -> d1.compareTo(d2));
        
        for (TimedEvent event : sortedEvents) {
            LocalDateTime eventDate = event.getTime().toLocalDate().atStartOfDay();
            eventsByDate.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
        }

        // Create calendar grid
        int row = 0;
        for (Map.Entry<LocalDateTime, List<TimedEvent>> entry : eventsByDate.entrySet()) {
            String date = entry.getKey().format(DATE_FORMATTER);
            List<TimedEvent> dayEvents = entry.getValue();

            // Sort events within the day by time
            dayEvents.sort((e1, e2) -> e1.getTime().compareTo(e2.getTime()));

            // Date header
            Label dateLabel = new Label(date);
            dateLabel.getStyleClass().add("calendar-date");
            calendarGrid.add(dateLabel, 0, row);

            // Events for the day
            VBox dayEventsBox = new VBox(5);
            dayEventsBox.getStyleClass().add("calendar-events");
            for (TimedEvent event : dayEvents) {
                Label eventLabel = new Label(String.format("%s - %s (%s)",
                    event.getTime().format(TIME_FORMATTER),
                    event.getName(),
                    event.getClass().getSimpleName()));
                eventLabel.getStyleClass().add("calendar-event");
                eventLabel.setWrapText(true);
                dayEventsBox.getChildren().add(eventLabel);
            }
            calendarGrid.add(dayEventsBox, 1, row);

            row++;
        }
    }
} 