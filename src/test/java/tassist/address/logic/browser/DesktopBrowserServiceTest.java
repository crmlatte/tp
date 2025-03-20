package tassist.address.logic.browser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.OpenCommand;

public class DesktopBrowserServiceTest {

    private final OpenCommand.DesktopBrowserService browserService = new OpenCommand.DesktopBrowserService();

    @Test
    public void openUrl_invalidUrl_throwsUriSyntaxException() {
        String invalidUrl = "not a url";

        assertThrows(URISyntaxException.class, () -> {
            browserService.openUrl(invalidUrl);
        });
    }

    @Test
    public void openUrl_malformedUrl_throwsUriSyntaxException() {
        String malformedUrl = "http://example.com\\invalid\\path";

        assertThrows(URISyntaxException.class, () -> {
            browserService.openUrl(malformedUrl);
        });
    }

    @Test
    public void openUrl_urlWithSpaces_throwsUriSyntaxException() {
        String urlWithSpaces = "http://example.com/path with spaces";

        assertThrows(URISyntaxException.class, () -> {
            browserService.openUrl(urlWithSpaces);
        });
    }

    @Test
    public void openUrl_urlWithInvalidChars_throwsUriSyntaxException() {
        String urlWithInvalidChars = "http://example.com/<>{}|\\^`";

        assertThrows(URISyntaxException.class, () -> {
            browserService.openUrl(urlWithInvalidChars);
        });
    }
}
