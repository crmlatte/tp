package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

import tassist.address.model.Model;
import tassist.address.model.timedevents.TimedEvent;

/**
 * Lists all timed events in the system.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_SUCCESS = "Listed all timed events:";
    public static final String MESSAGE_NO_EVENTS = "No timed events found.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<TimedEvent> timedEvents = model.getTimedEventList();

        if (timedEvents.isEmpty()) {
            return new CommandResult(MESSAGE_NO_EVENTS);
        }

        StringBuilder result = new StringBuilder();
        result.append(MESSAGE_SUCCESS).append("\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (int i = 0; i < timedEvents.size(); i++) {
            TimedEvent event = timedEvents.get(i);
            result.append(String.format("%d. %s - %s\n   Due: %s\n",
                    i + 1,
                    event.getName(),
                    event.getClass().getSimpleName(),
                    event.getTime().format(formatter)));
        }

        return new CommandResult(result.toString());
    }
} 