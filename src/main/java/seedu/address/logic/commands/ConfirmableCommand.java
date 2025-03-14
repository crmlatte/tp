package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Represents a command that requires user confirmation before execution.
 */
public interface ConfirmableCommand {
    /**
     * Executes the actual command after confirmation.
     */
    CommandResult executeConfirmed(Model model);

    /**
     * Returns the confirmation message to show the user.
     */
    String getConfirmationMessage();
}
