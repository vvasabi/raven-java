package net.kencochrane.sentry.spi;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.kencochrane.sentry.RavenUtils;

import org.apache.log4j.spi.LoggingEvent;

public class Log implements Serializable {

    private static final long serialVersionUID = -2449431636223102994L;

    private long timeStamp;

    private String message;

    private String loggerName;

    private int logLevel;

    private Throwable exception;

    private Map<String, Serializable> extra;

    public Log() {
        timeStamp = new Date().getTime();
        extra = new HashMap<String, Serializable>();
    }

    public Log(LoggingEvent event) {
        this();
        timeStamp = event.getTimeStamp();
        message = event.getRenderedMessage();
        loggerName = event.getLoggerName();

        //Need to divide by 1000 to keep consistent with sentry
        logLevel = event.getLevel().toInt() / 1000;

        if (event.getThrowableInformation() != null) {
            exception = event.getThrowableInformation().getThrowable();
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTimeStampString() {
        return RavenUtils.getTimestampString(timeStamp);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String name) {
        loggerName = name;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public void putExtra(String key, Serializable value) {
        extra.put(key, value);
    }

    public void removeExtra(String key) {
        extra.remove(key);
    }

    public Serializable getExtra(String key) {
        return extra.get(key);
    }

}
