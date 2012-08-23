package net.kencochrane.sentry.spi;

import org.json.simple.JSONObject;

public interface RavenPlugin {

    void preProcessLog(Log log);

    void postProcessRequestJSON(Log log, JSONObject json);

}
