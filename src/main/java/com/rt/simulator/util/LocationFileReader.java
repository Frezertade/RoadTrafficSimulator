package com.rt.simulator.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class LocationFileReader {

    private Properties properties;

    public LocationFileReader() throws IOException {
        properties = PropertyFileReader.readPropertyFile();
    }

    public List<String> readStreets() throws IOException {
        String streetFileName = properties.getProperty("com.rt.simulator.streets");
        File file = new File(streetFileName);
        return FileUtils.readLines(file);
    }

}
