package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
        private final Properties properties = new Properties();

        public PropertiesUtils() {
            try {
                this.readPropertyFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void readPropertyFile() throws IOException {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("application.properties");
            try {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}