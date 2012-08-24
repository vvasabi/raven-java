package net.kencochrane.sentry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.kencochrane.sentry.spi.RavenEvent;
import net.kencochrane.sentry.spi.RavenLogLevel;
import net.kencochrane.sentry.spi.RavenPlugin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This test validates JSON message bodies created by RavenClient.
 * 
 * @author vvasabi
 */
public class SentyClientJsonTest extends RavenClient {

    public SentyClientJsonTest() {
        super("http://public:secret@example.com/path/1");
    }

    @Test
    public void testPostProcessJSONUsingPlugin() {
        List<RavenPlugin> plugins = new ArrayList<RavenPlugin>();
        plugins.add(new RavenPlugin() {

            @Override
            public void postProcessRequestJSON(RavenEvent log, JSONObject json) {
                json.put("test", "123");
            }

            @Override
            public void preProcessEvent(RavenEvent log) {
            }

        });
        setPlugins(plugins);

        String messageString = "Test";
        String timestamp = RavenUtils.getTimestampString(new Date().getTime());
        String logger = "sentry.test";
        JSONObject result = buildJSON(new SimpleRavenEvent(""));
        assertEquals(result.get("test"), "123");
    }

    public void testMessageInterfaceObjectAdded() {
        String message = "Test";
        long timeStamp = new Date().getTime();
        String logger = "sentry.test";
        JSONObject result = buildJSON(new SimpleRavenEvent(timeStamp, message, logger, logger, RavenLogLevel.ERROR));
        assertTrue(result.containsKey("sentry.interfaces.Message"));
        JSONObject messageObject = (JSONObject)result.get("sentry.interfaces.Message");
        assertEquals(messageObject.get("message"), message);
        assertTrue(messageObject.get("params") instanceof JSONArray);
    }

    @Test
    public void testMessageInterfaceObjectNotAdded() {
        // when there is an exception, Message interface object should not be added
        String message = "Test";
        long timeStamp = new Date().getTime();
        String logger = "sentry.test";
        Throwable exception = new Exception("Test");
        JSONObject result = buildJSON(new SimpleRavenEvent(timeStamp, message, logger, logger, RavenLogLevel.ERROR, exception));
        assertFalse(result.containsKey("sentry.interfaces.Message"));
    }

}
