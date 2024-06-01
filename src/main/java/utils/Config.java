package utils;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties PROPERTIES = init("/local.config.properties");

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static Properties init(String path) {
        Properties props = new Properties();
        try {
            props.load(Config.class.getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props;
    }
}
