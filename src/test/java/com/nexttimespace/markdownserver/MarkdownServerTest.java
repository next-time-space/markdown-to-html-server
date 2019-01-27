package com.nexttimespace.markdownserver;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.nexttimespace.markdownserver.docs.utils.TestHelper;
import com.nexttimespace.markdownserver.docs.utils.UtilityFunctions;

import io.javalin.Javalin;
import io.javalin.staticfiles.Location;

public class MarkdownServerTest {
    private MarkdownServer markdownServer = new MarkdownServer();
    UtilityFunctions utils = Mockito.mock(UtilityFunctions.class);
    Javalin mockedApp = Mockito.mock(Javalin.class);
    
    @Test
    public void testSetupHttp() throws Exception {
        Mockito.doReturn("8080").when(utils).getConfigProperty("http.port");
        TestHelper.setField(markdownServer, "utilityFunctions", utils);
        TestHelper.callPrivateMethod(markdownServer, "setupHttp");
        
        Javalin app = (Javalin) TestHelper.getField(markdownServer, "app");
        Assertions.assertEquals(8080, app.port());
    }
    
    
    @Test
    public void testSetStaticDirectory() {
        Mockito.doReturn("/tmp").when(utils).getConfigProperty("resources.static");
        TestHelper.setField(markdownServer, "utilityFunctions", utils);
        TestHelper.setField(markdownServer, "app", mockedApp);
        TestHelper.callPrivateMethod(markdownServer, "setStaticDirectory");
        
        Mockito.verify(mockedApp).enableStaticFiles("/tmp", Location.EXTERNAL);
        
        Mockito.doReturn("").when(utils).getConfigProperty("resources.static");
        TestHelper.callPrivateMethod(markdownServer, "setStaticDirectory");
        
        Mockito.doReturn(null).when(utils).getConfigProperty("resources.static");
        TestHelper.callPrivateMethod(markdownServer, "setStaticDirectory");
    }
    
    @Test
    public void testGetContextPath() {
        Mockito.doReturn("/context").when(utils).getConfigProperty("http.contextPath");
        TestHelper.setField(markdownServer, "utilityFunctions", utils);
        String outContext = (String) TestHelper.callPrivateMethod(markdownServer, "getContextPath");
        Assertions.assertEquals(outContext, "/context");
        
        Mockito.doReturn("path").when(utils).getConfigProperty("http.contextPath");
        outContext = (String) TestHelper.callPrivateMethod(markdownServer, "getContextPath");
        Assertions.assertEquals(outContext, "/path");
        
        Mockito.doReturn(null).when(utils).getConfigProperty("http.contextPath");
        outContext = (String) TestHelper.callPrivateMethod(markdownServer, "getContextPath");
        Assertions.assertEquals(outContext, "/");
    }
    
    @Test
    public void testsetupHtmlFiles() {
        Mockito.doReturn("./site-data").when(utils).getConfigProperty("resources.site-data");
        TestHelper.setField(markdownServer, "utilityFunctions", utils);
        TestHelper.setField(markdownServer, "app", mockedApp);
        TestHelper.callPrivateMethod(markdownServer, "setupHtmlFiles");
        
        Mockito.doReturn("true").when(utils).getConfigProperty("resources.keep-exetension");
        TestHelper.callPrivateMethod(markdownServer, "setupHtmlFiles");
        
        Mockito.doReturn(".np").when(utils).getConfigProperty("resources.extension");
        boolean isDone = (boolean) TestHelper.callPrivateMethod(markdownServer, "setupHtmlFiles");
        Assertions.assertFalse(isDone);
    }
    
    @Test
    public void testIsValidConfig() {
        //Mockito.doReturn("./site-data").when(utils).getConfigProperty("resources.site-data");
        
        TestHelper.setField(markdownServer, "utilityFunctions", utils);
        Mockito.doReturn("12").when(utils).getConfigProperty("http.port");
        Mockito.doReturn("./site-data/static").when(utils).getConfigProperty("resources.static");
        Mockito.doReturn("./site-data").when(utils).getConfigProperty("resources.site-data");
        boolean isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertTrue(isValid);
        
        Mockito.doReturn("./nope").when(utils).getConfigProperty("resources.site-data");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn("./site-data/index.html").when(utils).getConfigProperty("resources.site-data");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn("").when(utils).getConfigProperty("resources.site-data");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn(null).when(utils).getConfigProperty("resources.site-data");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn(null).when(utils).getConfigProperty("resources.static");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        
        Mockito.doReturn("./site-data/index.html").when(utils).getConfigProperty("resources.static");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn("").when(utils).getConfigProperty("resources.static");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn("./nope").when(utils).getConfigProperty("resources.static");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        
        Mockito.doReturn("0").when(utils).getConfigProperty("http.port");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn("").when(utils).getConfigProperty("http.port");
        isValid = (boolean) TestHelper.callPrivateMethod(markdownServer, "isValidConfig");
        Assertions.assertFalse(isValid);
        
        Mockito.doReturn("12").when(utils).getConfigProperty("http.port");
    }
    
    @Test
    public void testStartServer() throws IOException {
        TestHelper.setField(markdownServer, "utilityFunctions", utils);
        Mockito.doReturn("12").when(utils).getConfigProperty("http.port");
        Mockito.doReturn("./site-data/static").when(utils).getConfigProperty("resources.static");
        Mockito.doReturn("./site-data").when(utils).getConfigProperty("resources.site-data");
        
        
        MarkdownServer mockedServer = Mockito.spy(MarkdownServer.class);
        Mockito.doNothing().when(mockedServer).setupHttp();
        Mockito.doNothing().when(mockedServer).setStaticDirectory();
        Mockito.doReturn(true).when(mockedServer).setupHtmlFiles();
        TestHelper.setField(mockedServer, "app", mockedApp);
        mockedServer.startServer();
        
        Mockito.verify(mockedApp).start();
    }
}
