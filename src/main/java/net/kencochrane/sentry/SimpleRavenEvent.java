package net.kencochrane.sentry;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import net.kencochrane.sentry.spi.RavenEvent;
import net.kencochrane.sentry.spi.RavenLogLevel;

import org.apache.log4j.spi.LoggingEvent;

public class SimpleRavenEvent implements RavenEvent {

    private static final long serialVersionUID = -2449431636223102994L;

    private long timeStamp;

    private String message;

    private String loggerName;
    
    private String culprit;

    private int logLevel;

    private Throwable exception;

    private Map<String, Serializable> data;

    private SimpleRavenEvent() {
        data = new HashMap<String, Serializable>();
    }
    
    public SimpleRavenEvent(String message) {
        this(new Date().getTime(), message, null, null, RavenLogLevel.INFO);
    }

    public SimpleRavenEvent(long timeStamp, String message, String loggerName, String culprit, int logLevel) {
        this(timeStamp, message, loggerName, culprit, logLevel, null);
    }

    public SimpleRavenEvent(long timeStamp, String message, String loggerName, String culprit, int logLevel, Throwable exception) {
        this();
        this.timeStamp = timeStamp;
        this.message = message;
        this.loggerName = loggerName;
        this.logLevel = logLevel;
        this.exception = exception;
    }

    public SimpleRavenEvent(LoggingEvent event) {
        this();
        timeStamp = event.getTimeStamp();
        message = event.getRenderedMessage();
        loggerName = event.getLoggerName();
        culprit = event.getLoggerName();

        // Need to divide by 1000 to keep consistent with sentry
        logLevel = event.getLevel().toInt() / 1000;

        if (event.getThrowableInformation() != null) {
            exception = event.getThrowableInformation().getThrowable();
        }
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLoggerName() {
        return loggerName;
    }
    
    @Override
    public String getCulprit() {
        return culprit;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public void putData(String key, Serializable value) {
        data.put(key, value);
    }

    @Override
    public void removeData(String key) {
        data.remove(key);
    }

    @Override
    public Serializable getData(String key) {
        return data.get(key);
    }

}
