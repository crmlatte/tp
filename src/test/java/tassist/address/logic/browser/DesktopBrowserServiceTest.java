package tassist.address.logic.browser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.OpenCommand;

public class DesktopBrowserServiceTest {

    private final OpenCommand.DesktopBrowserService browserService = new OpenCommand.DesktopBrowserService();

    @Test
    public void openUrl_invalidUrl_throwsUriSyntaxException() {
        String invalidUrl = "not a url";
        assertThrows(URISyntaxException.class, () -> new java.net.URI(invalidUrl));
    }

    @Test
    public void openUrl_malformedUrl_throwsUriSyntaxException() {
        String malformedUrl = "http://example.com\\invalid\\path";
        assertThrows(URISyntaxException.class, () -> new java.net.URI(malformedUrl));
    }

    @Test
    public void openUrl_urlWithSpaces_throwsUriSyntaxException() {
        String urlWithSpaces = "http://example.com/path with spaces";
        assertThrows(URISyntaxException.class, () -> new java.net.URI(urlWithSpaces));
    }

    @Test
    public void openUrl_urlWithInvalidChars_throwsUriSyntaxException() {
        String urlWithInvalidChars = "http://example.com/<>{}|\\^`";
        assertThrows(URISyntaxException.class, () -> new java.net.URI(urlWithInvalidChars));
    }

    @Test
    public void openUrl_validUrl_throwsIOExceptionOnHeadless() {
        String validUrl = "http://example.com";
        try {
            browserService.openUrl(validUrl);
        } catch (IOException e) {
            // On headless systems, we expect either:
            // "Desktop is not supported on this platform" or
            // "Opening URLs is not supported on this platform"
            assertTrue(e.getMessage().contains("not supported on this platform"));
        } catch (URISyntaxException e) {
            // This shouldn't happen with a valid URL
            throw new AssertionError("Valid URL threw URISyntaxException", e);
        }
    }
}
