package tassist.address.ui;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tassist.address.commons.core.GuiSettings;
import tassist.address.commons.core.LogsCenter;
import tassist.address.logic.Logic;
import tassist.address.logic.browser.BrowserService;
import tassist.address.logic.browser.DesktopBrowserService;
import tassist.address.logic.commands.CommandResult;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.logic.parser.exceptions.ParseException;


/**
 * The Main Window. Provides the basic application layout containing
 * <p>
 * a menu bar and space where other JavaFX elements can be placed.
 */

public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final String HELP_URL = "https://ay2425s2-cs2103t-w12-4.github.io/tp/UserGuide.html";
    private final Logger logger = LogsCenter.getLogger(getClass());
    private Stage primaryStage;
    private Logic logic;
    private final BrowserService browserService;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private CommandBox commandBox;
    private CalendarView calendarView;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private StackPane sendButtonPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem calendarMenuItem;

    @FXML
    private MenuItem themeMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private SplitPane splitPane;

    @FXML
    private StackPane mainContent;

    @FXML
    private StackPane calendarViewPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.browserService = new DesktopBrowserService();

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
        setAccelerator(calendarMenuItem, KeyCombination.valueOf("F3"));
        setAccelerator(themeMenuItem, KeyCombination.valueOf("F2"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        calendarView = new CalendarView(logic.getTimedEventList(), logic);

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        // Add send button
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> {
            try {
                String commandText = commandBox.getCommandText();
                if (!commandText.isEmpty()) {
                    executeCommand(commandText);
                    commandBox.clearCommandText();
                }
            } catch (CommandException | ParseException e) {
                logger.warning("Error executing command: " + e.getMessage());
                resultDisplay.setFeedbackToUser(e.getMessage());
            }
        });
        sendButtonPlaceholder.getChildren().add(sendButton);
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        try {

            browserService.openUrl(HELP_URL);

        } catch (IOException | URISyntaxException e) {

            logger.warning("Failed to open help window: " + e.getMessage());

            resultDisplay.setFeedbackToUser("Failed to open help window.");

        }
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see tassist.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            // Refresh calendar view if it's visible
            if (calendarViewPlaceholder.isVisible()) {
                calendarView.updateEvents(logic.getTimedEventList());
            }

            if (commandResult.isShowHelp()) {
                handleHelp();
            }
            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    @FXML
    private void handleStudentCardsView() {
        // Show split pane and hide calendar view
        splitPane.setVisible(true);
        splitPane.setManaged(true);
        calendarViewPlaceholder.setVisible(false);
        calendarViewPlaceholder.setManaged(false);

        // Restore person list panel and split pane position
        personListPanelPlaceholder.getChildren().clear();
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());
        splitPane.setDividerPositions(0.35);

        // Restore result display
        resultDisplayPlaceholder.getChildren().clear();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        // Restore command box and send button
        commandBoxPlaceholder.getChildren().clear();
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> {
            try {
                String commandText = commandBox.getCommandText();
                if (!commandText.isEmpty()) {
                    executeCommand(commandText);
                    commandBox.clearCommandText();
                }
            } catch (CommandException | ParseException e) {
                logger.warning("Error executing command: " + e.getMessage());
                resultDisplay.setFeedbackToUser(e.getMessage());
            }
        });
        sendButtonPlaceholder.getChildren().clear();
        sendButtonPlaceholder.getChildren().add(sendButton);

        // Request focus on command box
        commandBox.requestFocus();
    }

    @FXML
    private void handleCalendarView() {
        // Toggle between views
        if (calendarViewPlaceholder.isVisible()) {
            // If calendar is visible, switch back to student cards view
            handleStudentCardsView();
        } else {
            // If calendar is hidden, switch to calendar view
            splitPane.setVisible(false);
            splitPane.setManaged(false);
            calendarViewPlaceholder.setVisible(true);
            calendarViewPlaceholder.setManaged(true);
            // Set up calendar view
            calendarViewPlaceholder.getChildren().clear();
            calendarViewPlaceholder.getChildren().add(calendarView.getRoot());
            // Refresh the events in calendar view
            calendarView.updateEvents(logic.getTimedEventList());
            // Request focus on command box
            commandBox.requestFocus();
        }
    }

    @FXML
    private void handleDarkTheme() {
        Scene scene = primaryStage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());
    }

    @FXML
    private void handleBrightTheme() {
        Scene scene = primaryStage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/view/BrightTheme.css").toExternalForm());
    }

    @FXML
    private void handlePinkTheme() {
        Scene scene = primaryStage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/view/PinkTheme.css").toExternalForm());
    }

    @FXML
    private void handleThemeCycle() {
        Scene scene = primaryStage.getScene();
        String currentTheme = "";

        // Find the current theme by checking all stylesheets
        for (String stylesheet : scene.getStylesheets()) {
            if (stylesheet.contains("DarkTheme")) {
                currentTheme = "DarkTheme";
                break;
            } else if (stylesheet.contains("BrightTheme")) {
                currentTheme = "BrightTheme";
                break;
            } else if (stylesheet.contains("PinkTheme")) {
                currentTheme = "PinkTheme";
                break;
            }
        }

        // Cycle through themes
        switch (currentTheme) {
        case "DarkTheme":
            handleBrightTheme();
            break;
        case "BrightTheme":
            handlePinkTheme();
            break;
        case "PinkTheme":
            handleDarkTheme();
            break;
        default:
            handleDarkTheme();
            break;
        }
    }
}
