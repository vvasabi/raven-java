package net.kencochrane.sentry.spi;

import java.io.Serializable;

public interface RavenEvent extends Serializable {

    long getTimeStamp();

    String getMessage();

    String getLoggerName();
    
    String getCulprit();

    int getLogLevel();

    Throwable getException();

    void putData(String key, Serializable value);

    void removeData(String key);

    Serializable getData(String key);

}
