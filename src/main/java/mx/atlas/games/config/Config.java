package mx.atlas.games.config;

import mx.atlas.games.GamesSalesApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    public static Properties properties;

    static {
        properties = new Properties();
        try (InputStream is = GamesSalesApplication.class.getResourceAsStream("application.properties")) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
