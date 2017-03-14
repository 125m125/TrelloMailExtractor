package de._125m125.trelloMail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TrelloMailConfig {
    private final Properties prop;

    public TrelloMailConfig(final String path) throws FileNotFoundException, IOException {
        this.prop = new Properties();
        try (final InputStream is = new FileInputStream(path)) {
            this.prop.load(is);
        }
    }

    public String getProperty(final String name) {
        return getProperty(name, false);
    }

    public String getProperty(final String name, final boolean required) {
        final String result = this.prop.getProperty(name);
        if (result == null) {
            throw new RuntimeException("Missing property in config: " + name);
        }
        return result;
    }

    public String getPropertyOrDefault(final String name, final String defaultValue) {
        return this.prop.getProperty(name, defaultValue);
    }
}
