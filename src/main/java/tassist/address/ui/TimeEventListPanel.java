package tassist.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import tassist.address.model.timedevents.TimedEvent;

/**
 * Panel containing the list of timed events.
 */
public class TimeEventListPanel extends UiPart<Region> {
    private static final String FXML = "TimeEventListPanel.fxml";

    @FXML
    private ListView<TimedEvent> timeEventListView;

    /**
     * Creates a {@code TimeEventListPanel} with the given {@code ObservableList}.
     */
    public TimeEventListPanel(ObservableList<TimedEvent> timeEventList) {
        super(FXML);
        timeEventListView.setItems(timeEventList);
        timeEventListView.setCellFactory(listView -> new TimeEventListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code TimedEvent} using a {@code TimeEventCard}.
     */
    class TimeEventListViewCell extends ListCell<TimedEvent> {
        @Override
        protected void updateItem(TimedEvent timeEvent, boolean empty) {
            super.updateItem(timeEvent, empty);

            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new TimeEventCard(timeEvent, getIndex() + 1).getRoot());
            }
        }
    }
} 