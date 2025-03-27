package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tassist.address.logic.browser.BrowserService;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code HelpCommand}.
 */
public class HelpCommandTest {

    private Model model;
    private TestBrowserService browserService;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        browserService = new TestBrowserService();
    }

    @Test
    public void execute_success() {
        HelpCommand helpCommand = new HelpCommand(browserService);
        CommandResult result = helpCommand.execute(model);

        assertEquals(HelpCommand.SHOWING_HELP_MESSAGE, result.getFeedbackToUser());
        assertEquals(HelpCommand.HELP_URL, browserService.getLastUrlOpened());
    }

    @Test
    public void execute_browserServiceThrowsException_returnsFailureMessage() {
        HelpCommand helpCommand = new HelpCommand(new FailingBrowserService());
        CommandResult result = helpCommand.execute(model);

        assertEquals(HelpCommand.MESSAGE_OPEN_HELP_FAILURE, result.getFeedbackToUser());
    }

    @Test
    public void equals() {
        HelpCommand helpFirstCommand = new HelpCommand(browserService);
        HelpCommand helpSecondCommand = new HelpCommand(new TestBrowserService());

        // same object -> returns true
        assertEquals(helpFirstCommand, helpFirstCommand);

        // same values -> returns true
        HelpCommand helpFirstCommandCopy = new HelpCommand(browserService);
        assertEquals(helpFirstCommand, helpFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, helpFirstCommand);

        // null -> returns false
        assertNotEquals(null, helpFirstCommand);

        // different browser service -> returns false
        assertNotEquals(helpFirstCommand, helpSecondCommand);
    }

    /**
     * Test implementation of BrowserService that records URLs instead of opening them.
     */
    private static class TestBrowserService implements BrowserService {
        private List<String> urlsOpened = new ArrayList<>();

        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            urlsOpened.add(url);
        }

        /**
         * Returns the list of URLs that would have been opened.
         */
        public List<String> getUrlsOpened() {
            return urlsOpened;
        }

        /**
         * Returns the last URL that would have been opened, or null if none.
         */
        public String getLastUrlOpened() {
            return urlsOpened.isEmpty() ? null : urlsOpened.get(urlsOpened.size() - 1);
        }

        /**
         * Clears the list of opened URLs.
         */
        public void clear() {
            urlsOpened.clear();
        }
    }

    /**
     * Test implementation of BrowserService that always throws an exception.
     */
    private static class FailingBrowserService implements BrowserService {
        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            throw new IOException("Test exception");
        }
    }
}
