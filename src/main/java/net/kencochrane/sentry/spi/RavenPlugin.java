package net.kencochrane.sentry.spi;

import org.json.simple.JSONObject;

public interface RavenPlugin {

    void preProcessEvent(RavenEvent event);

    void postProcessRequestJSON(RavenEvent event, JSONObject json);

}
