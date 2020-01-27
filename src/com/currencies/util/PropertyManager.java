/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.currencies.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

    private static PropertyManager instance;
    private Properties configProperties;

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    public void loadProperties(String filePath) {
        Properties properties = null;
        try {
            InputStream inputStream = new FileInputStream(filePath);
            properties = new Properties();
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setConfigProperties(properties);
    }

    public String getProperty(String property) {
        if (configProperties == null) {
            return "";
        }
        return configProperties.getProperty(property);
    }

    /**
     * @return the configProperties
     */
    public Properties getConfigProperties() {
        return configProperties;
    }

    /**
     * @param configProperties the configProperties to set
     */
    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }
}
