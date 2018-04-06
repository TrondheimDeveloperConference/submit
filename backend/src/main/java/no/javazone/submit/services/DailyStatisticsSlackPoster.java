package no.javazone.submit.services;

import no.javazone.submit.config.SleepingPillConfiguration;
import no.javazone.submit.integrations.slack.SlackClient;
import no.javazone.submit.integrations.sleepingpill.SleepingPillClient;
import no.javazone.submit.integrations.sleepingpill.model.get.Sessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DailyStatisticsSlackPoster {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final SleepingPillClient sleepingPillClient;
    private final SlackClient slackClient;
    private final SleepingPillConfiguration sleepingPillConfiguration;

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    @Autowired
    public DailyStatisticsSlackPoster(SleepingPillClient sleepingPillClient,
                                      SlackClient slackClient,
                                      SleepingPillConfiguration sleepingPillConfiguration) {
        this.sleepingPillClient = sleepingPillClient;
        this.slackClient = slackClient;
        this.sleepingPillConfiguration = sleepingPillConfiguration;
        setupRecurringJob();
    }

    private void setupRecurringJob() {
        long timeToFirstExecution = calculateMillisecondsUntilTeatime();
        SCHEDULER.scheduleAtFixedRate(this::postStatistics, timeToFirstExecution, 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);
        LOG.info("Scheduled daily statistics job to 16:00 each day, that should be " + timeToFirstExecution + "ms from now...");
    }

    private static long calculateMillisecondsUntilTeatime() {
        ZonedDateTime now = ZonedDateTime.now();
        ZoneId zoneId = ZoneId.of("Europe/Oslo");
        ZonedDateTime teatime = now.withHour(16).withMinute(0).withSecond(0);
        if (teatime.isBefore(now)) teatime = teatime.plusDays(1);

        return Duration.between(now, teatime).toMillis();
    }

    private void postStatistics() {
        String conferenceId = sleepingPillClient.getConferences().getIdFromSlug(sleepingPillConfiguration.activeYear);
        Sessions talks = sleepingPillClient.getTalksByConferenceId(conferenceId);
        slackClient.postStatistics(talks);
    }
}
