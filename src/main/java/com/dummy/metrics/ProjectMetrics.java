package com.dummy.metrics;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProjectMetrics {
    private static final Logger log = LoggerFactory.getLogger(ProjectMetrics.class);

    private static ProjectMetrics instance = new ProjectMetrics();

    private final MetricRegistry metricRegistry;
    private final Meter messageRate;

    public static ProjectMetrics getInstance() {
        return instance;
    }

    private ProjectMetrics() {
        this.metricRegistry = new MetricRegistry();
        messageRate = metricRegistry.meter("allMessages");
    }

    public static void init(){
        int metricIntervalsec=10;

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> log.info(ProjectMetrics.getInstance().toString()),
                10,
                metricIntervalsec,
                TimeUnit.SECONDS
        );
    }

    @Override
    public String toString(){
        return String.format("{\"all_messages\": %d}",
                getAllMessages()
        );
    }

    public long getAllMessages(){
        return messageRate.getCount();
    }

    public void incMessages(){messageRate.mark();}

}
