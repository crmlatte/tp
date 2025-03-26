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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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

        // Group events by date
        Map<String, List<TimedEvent>> eventsByDate = new HashMap<>();
        for (TimedEvent event : events) {
            String date = event.getTime().format(DATE_FORMATTER);
            eventsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
        }

        // Create calendar grid
        int row = 0;
        for (Map.Entry<String, List<TimedEvent>> entry : eventsByDate.entrySet()) {
            String date = entry.getKey();
            List<TimedEvent> dayEvents = entry.getValue();

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