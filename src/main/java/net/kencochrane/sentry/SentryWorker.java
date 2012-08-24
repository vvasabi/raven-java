package net.kencochrane.sentry;

import net.kencochrane.sentry.spi.RavenEvent;
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

    private BlockingQueue<RavenEvent> queue;

    public SentryWorker(BlockingQueue<RavenEvent> queue, String sentryDSN, String proxy, boolean naiveSsl, List<RavenPlugin> plugins)
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
                sendToSentry(queue.take());
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

    public void sendToSentry(RavenEvent log)
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
