/*
 * Copyright 2019 Next Time Space.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nexttimespace.markdownserver.docs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

public class UtilityFunctions {

    private Map<String, Object> confProperties;
    private static Logger logger = Logger.getLogger(UtilityFunctions.class);

    private Map<String, Object> getConfProperties() {
        try {
            if (confProperties == null) {
                confProperties = setupProperty();
            }
            return confProperties;
        } catch (Exception e) {
            logger.error("Error getting config", e);
            return Collections.emptyMap();
        }
    }

    public String getConfigProperty(String key) {
        String value = null;
        if (key.contains(".")) {
            Map<String, Object> conf = getConfProperties();
            Map<String, Object> tempConf = conf;
            String[] keys = key.split("\\.");
            for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                Object innerValue = tempConf.get(keys[keyIndex]);
                if (keyIndex == keys.length - 1) {
                    value = innerValue != null ? innerValue.toString() : null;
                } else {
                    if (innerValue != null && innerValue instanceof Map) {
                        tempConf = (Map) innerValue;
                    } else {
                        value = null;
                        break;
                    }
                }
            }
        } else {
            Object valueObject = getConfProperties().get(key);
            value = valueObject != null ? valueObject.toString() : null;
        }
        return value;
    }

    private Map<String, Object> setupProperty() throws Exception {
        Yaml yaml = new Yaml();
        String confFile = getExecutablePath() + "conf.yml";
        InputStream inputStream = new FileInputStream(new File(confFile));
        return yaml.load(inputStream);
    }

    public String getExecutablePath() throws URISyntaxException {
        URL jarLocationUrl = UtilityFunctions.class.getProtectionDomain().getCodeSource().getLocation();
        String exeLocation = jarLocationUrl.toString();
        exeLocation = exeLocation.replace("file:", "").replace("jar:file:", "");
        if (exeLocation.endsWith(".jar")) {
            exeLocation = new File(exeLocation).getParent();
        }
        exeLocation = exeLocation.endsWith("/") ? exeLocation : exeLocation + "/";
        return exeLocation;
    }

}
