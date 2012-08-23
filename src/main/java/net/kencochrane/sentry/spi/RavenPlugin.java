package net.kencochrane.sentry.spi;

import org.json.simple.JSONObject;

public interface RavenPlugin {

    void postProcessRequestJSON(JSONObject json);

}
