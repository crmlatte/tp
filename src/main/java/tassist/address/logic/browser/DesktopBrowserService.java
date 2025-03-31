package tassist.address.logic.browser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Default implementation of BrowserService that uses Desktop.browse().
 */
public class DesktopBrowserService implements BrowserService {
    @Override
    public void openUrl(String url) throws IOException, URISyntaxException {
        // First validate the URL by creating a URI object
        URI uri = new URI(url);

        // Check if Desktop is supported
        if (!Desktop.isDesktopSupported()) {
            throw new IOException("Desktop is not supported on this platform");
        }

        Desktop desktop = Desktop.getDesktop();
        // Check if browsing is supported
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            throw new IOException("Opening URLs is not supported on this platform");
        }

        desktop.browse(uri);
    }
}
