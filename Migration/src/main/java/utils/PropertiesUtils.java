package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
        private final Properties properties = new Properties();

        public PropertiesUtils() {
            this.readPropertyFile();
        }

        private void readPropertyFile() {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("application.properties");
            try {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void defineProperties() {

        }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}