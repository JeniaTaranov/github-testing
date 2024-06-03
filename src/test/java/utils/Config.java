package utils;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties PROPERTIES = init("/local.properties");

    public static String getProperty(String key) {
        String property = PROPERTIES.getProperty(key);

        if (property == null){
            throw new RuntimeException(
                    "Property " + key + " is missing in local.properties file, application cannot run");
        }

        return property;
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
