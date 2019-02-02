package com.rt.simulator.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {

    private static final Logger logger = Logger.getLogger(PropertyFileReader.class);
    private static Properties properties = new Properties();

    public static Properties readPropertyFile() throws IOException {
        if(properties.isEmpty()) {
            InputStream inputStream = PropertyFileReader.class.getClassLoader().getResourceAsStream("applications.properties");
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                logger.error(e);
                throw e;
            }finally {
                if(inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return properties;
    }
}
