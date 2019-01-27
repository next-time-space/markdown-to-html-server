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

package com.nexttimespace.markdownserver;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;

import com.nexttimespace.markdownserver.docs.utils.UtilityFunctions;

import io.javalin.Javalin;
import io.javalin.staticfiles.Location;

public class MarkdownServer {
    private static Logger logger = Logger.getLogger(MarkdownServer.class);
    private UtilityFunctions utilityFunctions = new UtilityFunctions();
    private Javalin app;
    
    public void setupHttp() {
        String portString = utilityFunctions.getConfigProperty("http.port");
        int port = Integer.parseInt(portString);
        app = Javalin.create()
                .contextPath(getContextPath()).port(port);
    }
    
    public void setStaticDirectory() throws IOException {
        String staticFolderPath = utilityFunctions.getConfigProperty("resources.static");
        if(staticFolderPath != null && !staticFolderPath.isEmpty()) {
            File staticDirectory = new File(staticFolderPath);
            logger.info("Using folder as staic folder be aware all will be served, " + staticDirectory.getCanonicalPath());
            app.enableStaticFiles(staticDirectory.getCanonicalPath(), Location.EXTERNAL);
        }
    }
    
    private String getContextPath() {
        String contextPath = utilityFunctions.getConfigProperty("http.contextPath");
        contextPath = contextPath != null && !contextPath.isEmpty() ? 
                contextPath.startsWith("/") ? contextPath : "/" 
            + contextPath : "/";
        return contextPath;
    }
    
    public boolean setupHtmlFiles() {
        String siteDataFolder = utilityFunctions.getConfigProperty("resources.site-data");
        String extension = utilityFunctions.getConfigProperty("resources.extension");
        extension = (extension != null && !extension.isEmpty()) ? extension : ".html";
        String extensionRegex =  "\\" + extension;
        boolean keepExtension = "true".equals(utilityFunctions.getConfigProperty("resources.keep-exetension"));
        File basePath = new File(siteDataFolder);
        
        Collection<File> files = FileUtils.listFiles(
                basePath, 
                new RegexFileFilter(".*" + extensionRegex), 
                DirectoryFileFilter.DIRECTORY
        );
        
        if(files.isEmpty()) {
            logger.error("No matching web file found.");
            return false;
        }
        
        for(File file: files) {
            if(file.getName().endsWith("index" + extension)) {
                app.get("/", ctx -> {
                    ctx.header("server", "");
                    ctx.html(FileUtils.readFileToString(file, "UTF-8"));
                });
            } else {
                String requestMapping = file.getPath().replace(siteDataFolder, "");
                if(!keepExtension) {
                    requestMapping = requestMapping.replace(extension, "");
                }
                app.get(requestMapping, ctx -> {
                    ctx.header("server", "");
                    ctx.html(FileUtils.readFileToString(file, "UTF-8"));
                });
            }
        }
        return true;
    }
    
    private boolean isValidConfig() {
        int port = -1;
        try {
            String portString = utilityFunctions.getConfigProperty("http.port");
            port = Integer.parseInt(portString);
            if(port <= 0) {
                logger.error("Configuration validation failed, port invalid");
                return false;
            }
        } catch(Exception e) {
            logger.error("Configuration validation failed, port invalid");
            return false;
        }
        
        String staticFolderPath = utilityFunctions.getConfigProperty("resources.static");
        if(staticFolderPath != null && !staticFolderPath.isEmpty()) {
            File staticDirectory = new File(staticFolderPath);
            if(!staticDirectory.exists() || !staticDirectory.isDirectory()) {
                logger.error("Configuration validation failed, resources.static directory does not exist");
                return false;
            }
        }
        
        String siteDataFolder = utilityFunctions.getConfigProperty("resources.site-data");
        
        if(siteDataFolder == null || siteDataFolder.isEmpty()) {
            logger.error("Configuration validation failed, resources.site-data does not exist");
           return false;
        } else {
            File siteDataDirectory = new File(siteDataFolder);
            if(!siteDataDirectory.exists() || !siteDataDirectory.isDirectory()) {
                logger.error("Configuration validation failed, resources.site-data directory does not exist");
                return false;
            }
        }
        return true;
    }
    
    public void startServer() {
        try {
            logger.info("Starting server");
            if(isValidConfig()) {
                setupHttp();
                setStaticDirectory();
                boolean isSetupDone = setupHtmlFiles();
                if(isSetupDone) {
                    app.disableStartupBanner();
                    app.start();
                    logger.info("Server started successfully.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error starting server", e);
        }
    }

}
