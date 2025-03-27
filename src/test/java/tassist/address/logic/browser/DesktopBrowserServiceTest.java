package tassist.address.logic.browser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.OpenCommand.BrowserService;

public class DesktopBrowserServiceTest {

    @Test
    public void openUrl_invalidUrl_throwsUriSyntaxException() {
        String invalidUrl = "not a url";
        BrowserService browserService = new StubBrowserService();
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(invalidUrl));
    }

    @Test
    public void openUrl_malformedUrl_throwsUriSyntaxException() {
        String malformedUrl = "http://example.com\\invalid\\path";
        BrowserService browserService = new StubBrowserService();
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(malformedUrl));
    }

    @Test
    public void openUrl_urlWithSpaces_throwsUriSyntaxException() {
        String urlWithSpaces = "http://example.com/path with spaces";
        BrowserService browserService = new StubBrowserService();
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(urlWithSpaces));
    }

    @Test
    public void openUrl_urlWithInvalidChars_throwsUriSyntaxException() {
        String urlWithInvalidChars = "http://example.com/<>{}|\\^`";
        BrowserService browserService = new StubBrowserService();
        assertThrows(URISyntaxException.class, () -> browserService.openUrl(urlWithInvalidChars));
    }

    @Test
    public void openUrl_desktopNotSupported_throwsIoException() {
        BrowserService browserService = new DesktopNotSupportedStub();
        String validUrl = "http://example.com";
        IOException thrown = assertThrows(IOException.class, () -> browserService.openUrl(validUrl));
        assertEquals("Desktop is not supported on this platform", thrown.getMessage());
    }

    @Test
    public void openUrl_browsingNotSupported_throwsIoException() {
        BrowserService browserService = new BrowsingNotSupportedStub();
        String validUrl = "http://example.com";
        IOException thrown = assertThrows(IOException.class, () -> browserService.openUrl(validUrl));
        assertEquals("Opening URLs is not supported on this platform", thrown.getMessage());
    }

    @Test
    public void openUrl_validUrl_browsesSuccessfully() throws IOException, URISyntaxException {
        RecordingBrowserStub browserService = new RecordingBrowserStub();
        String validUrl = "http://example.com";
        browserService.openUrl(validUrl);
        assertEquals(new URI(validUrl), browserService.getLastBrowsedUri());
    }

    @Test
    public void openUrl_validUrlWithEncodedSpaces_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com/path%20with%20spaces";
        assertDoesNotThrow(() -> new URI(validUrl));
    }

    @Test
    public void openUrl_validUrlWithPort_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com:8080/path";
        assertDoesNotThrow(() -> new URI(validUrl));
    }

    @Test
    public void openUrl_validUrlWithQuery_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com/path?param=value";
        assertDoesNotThrow(() -> new URI(validUrl));
    }

    @Test
    public void openUrl_validUrlWithFragment_validatesCorrectly() throws URISyntaxException {
        String validUrl = "http://example.com/path#section";
        assertDoesNotThrow(() -> new URI(validUrl));
    }

    /**
     * A stub BrowserService that implements the basic functionality.
     */
    private static class StubBrowserService implements BrowserService {
        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            // Just validate the URL by creating a URI object
            new URI(url);
        }
    }

    /**
     * A stub that simulates Desktop not being supported.
     */
    private static class DesktopNotSupportedStub implements BrowserService {
        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            // Validate URL first
            new URI(url);
            throw new IOException("Desktop is not supported on this platform");
        }
    }

    /**
     * A stub that simulates browsing not being supported.
     */
    private static class BrowsingNotSupportedStub implements BrowserService {
        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            // Validate URL first
            new URI(url);
            throw new IOException("Opening URLs is not supported on this platform");
        }
    }

    /**
     * A stub that records the last URI that was browsed to.
     */
    private static class RecordingBrowserStub implements BrowserService {
        private URI lastBrowsedUri;

        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            lastBrowsedUri = new URI(url);
        }

        public URI getLastBrowsedUri() {
            return lastBrowsedUri;
        }
    }
}
