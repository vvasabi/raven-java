package net.kencochrane.sentry;

import net.kencochrane.sentry.spi.RavenEvent;
import net.kencochrane.sentry.spi.RavenPlugin;

import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: mphilpot
 * Date: 3/29/12
 */
public class SentryQueue
{
    private static SentryQueue ourInstance = new SentryQueue();
    private static BlockingQueue<RavenEvent> queue;

    private SentryWorker worker;
    private boolean blocking;
    private List<RavenPlugin> plugins;

    public static SentryQueue getInstance()
    {
        return ourInstance;
    }

    private SentryQueue()
    {
        queue = null;

        worker = null;

    }

    public void shutdown()
    {
        worker.shutdown();
        worker.interrupt();
    }

    public synchronized boolean isSetup()
    {
        return (queue != null);
    }

    public synchronized void setup(String sentryDSN, String proxy, int queueSize, boolean blocking, boolean naiveSsl)
    {
        queue = new LinkedBlockingQueue<RavenEvent>(queueSize);
        this.blocking = blocking;

        plugins = loadPlugins();
        worker = new SentryWorker(queue, sentryDSN, proxy, naiveSsl, plugins);
        worker.start();
    }

    private List<RavenPlugin> loadPlugins()
    {
        List<RavenPlugin> plugins = new ArrayList<RavenPlugin>();
        Iterator<RavenPlugin> iterator = ServiceLoader.load(RavenPlugin.class).iterator();
        while (iterator.hasNext())
        {
            plugins.add(iterator.next());
        }
        System.out.println("Raven plugins found: " + plugins);
        return plugins;
    }

    public void addEvent(LoggingEvent le)
    {
        try
        {
            RavenEvent log = new SimpleRavenEvent(le);
            for (RavenPlugin plugin : plugins) {
                plugin.preProcessEvent(log);
            }

            if(blocking)
            {
                queue.put(log);
            }
            else
            {
                queue.add(log);
            }
        }
        catch(IllegalStateException e)
        {
            System.err.println("Sentry Queue Full :: " + le);
        }
        catch(InterruptedException e)
        {
            System.err.println("Sentry Queue Interrupted :: "+ le);
        }
    }
}
