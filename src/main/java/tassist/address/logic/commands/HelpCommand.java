package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URISyntaxException;

import tassist.address.logic.browser.BrowserService;
import tassist.address.logic.browser.DesktopBrowserService;
import tassist.address.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";
    public static final String HELP_URL = "https://ay2425s2-cs2103t-w12-4.github.io/tp/UserGuide.html";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example:\n"
            + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window in browser.";
    public static final String MESSAGE_OPEN_HELP_FAILURE = "Failed to open help window in browser.";

    private final BrowserService browserService;

    /**
     * Constructs a HelpCommand with default browser service.
     */
    public HelpCommand() {
        this(new DesktopBrowserService());
    }

    /**
     * Constructs a HelpCommand with a specified browser service and help window.
     *
     * @param browserService The browser service to handle opening URLs.
     */
    public HelpCommand(BrowserService browserService) {
        this.browserService = browserService;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        try {
            browserService.openUrl(HELP_URL);
        } catch (IOException | URISyntaxException e) {
            return new CommandResult(MESSAGE_OPEN_HELP_FAILURE, true, false);
        }

        return new CommandResult(SHOWING_HELP_MESSAGE, false, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof HelpCommand)) {
            return false;
        }

        HelpCommand otherHelpCommand = (HelpCommand) other;
        return browserService.equals(otherHelpCommand.browserService);
    }
}
