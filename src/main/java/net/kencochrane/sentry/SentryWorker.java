package net.kencochrane.sentry;

import net.kencochrane.sentry.spi.Log;
import net.kencochrane.sentry.spi.RavenPlugin;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * User: mphilpot
 * Date: 3/29/12
 */
public class SentryWorker extends Thread
{
    private boolean shouldShutdown;

    private RavenClient client;

    private BlockingQueue<Log> queue;

    public SentryWorker(BlockingQueue<Log> queue, String sentryDSN, String proxy, boolean naiveSsl, List<RavenPlugin> plugins)
    {
        this.shouldShutdown = false;
        this.queue = queue;
        this.client = new RavenClient(sentryDSN, proxy, naiveSsl);
        this.client.setPlugins(plugins);
    }

    @Override
    public void run()
    {
        while(!shouldShutdown)
        {
            try
            {
                Log log = queue.take();
                sendToSentry(log);
            }
            catch (InterruptedException e)
            {
                // Thread interrupted... probably shutting down
            }
        }
    }

    public void shutdown()
    {
        shouldShutdown = true;
    }

    public void sendToSentry(Log log)
    {
        synchronized (this)
        {
            try
            {
                // send the message to the sentry server
                client.captureLog(log);
            } catch (Exception e)
            {
                // Can we tell if there is another logger to send the event to?
                System.err.println(e);
            }
        }
    }

}
