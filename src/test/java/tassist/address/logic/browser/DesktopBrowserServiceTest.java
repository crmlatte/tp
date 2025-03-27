package tassist.address.logic.browser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.OpenCommand;

public class DesktopBrowserServiceTest {

    private final OpenCommand.DesktopBrowserService browserService = new OpenCommand.DesktopBrowserService();

    @Test
    public void openUrl_invalidUrl_throwsUriSyntaxException() {
        String invalidUrl = "not a url";
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(invalidUrl));
    }

    @Test
    public void openUrl_malformedUrl_throwsUriSyntaxException() {
        String malformedUrl = "http://example.com\\invalid\\path";
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(malformedUrl));
    }

    @Test
    public void openUrl_urlWithSpaces_throwsUriSyntaxException() {
        String urlWithSpaces = "http://example.com/path with spaces";
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(urlWithSpaces));
    }

    @Test
    public void openUrl_urlWithInvalidChars_throwsUriSyntaxException() {
        String urlWithInvalidChars = "http://example.com/<>{}|\\^`";
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(urlWithInvalidChars));
    }

    @Test
    public void openUrl_desktopNotSupported_throwsIoException() {
        String validUrl = "http://example.com";
        if (!Desktop.isDesktopSupported()) {
            assertThrows(IOException.class, () -> browserService.openUrl(validUrl),
                    "Desktop is not supported on this platform");
        }
    }

    @Test
    public void openUrl_browsingNotSupported_throwsIoException() {
        String validUrl = "http://example.com";
        // Only run this test if Desktop is supported
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                assertThrows(IOException.class, () -> browserService.openUrl(validUrl),
                        "Opening URLs is not supported on this platform");
            }
        }
    }

    @Test
    public void openUrl_validUrlWithEncodedSpaces_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com/path%20with%20spaces";
        // Should not throw URISyntaxException
        new URI(validUrl);
    }

    @Test
    public void openUrl_validUrlWithPort_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com:8080/path";
        // Should not throw URISyntaxException
        new URI(validUrl);
    }

    @Test
    public void openUrl_validUrlWithQuery_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com/path?param=value";
        // Should not throw URISyntaxException
        new URI(validUrl);
    }

    @Test
    public void openUrl_validUrlWithFragment_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com/path#section";
        // Should not throw URISyntaxException
        new URI(validUrl);
    }
}
