package tassist.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import tassist.address.model.timedevents.TimedEvent;

/**
 * An UI component that displays information of a {@code TimedEvent}.
 */
public class TimeEventCard extends UiPart<Region> {

    private static final String FXML = "TimeEventListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final TimedEvent timeEvent;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label description;
    @FXML
    private Label time;
    @FXML
    private Label remainingTime;

    /**
     * Creates a {@code TimeEventCard} with the given {@code TimedEvent} and index to display.
     */
    public TimeEventCard(TimedEvent timeEvent, int displayedIndex) {
        super(FXML);
        this.timeEvent = timeEvent;
        id.setText(displayedIndex + ". ");
        name.setText(timeEvent.getName());
        description.setText(timeEvent.getDescription());
        time.setText(timeEvent.getTime().toString());
        remainingTime.setText(timeEvent.calculateRemainingTime());
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TimeEventCard)) {
            return false;
        }

        // state check
        TimeEventCard card = (TimeEventCard) other;
        return id.getText().equals(card.id.getText())
                && timeEvent.equals(card.timeEvent);
    }

    @Override
    public int hashCode() {
        return timeEvent.hashCode();
    }
} 