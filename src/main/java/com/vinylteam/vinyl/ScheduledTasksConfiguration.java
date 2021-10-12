package com.vinylteam.vinyl;

import com.vinylteam.vinyl.util.Updater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@ConditionalOnProperty(value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableScheduling
@Slf4j
public class ScheduledTasksConfiguration {

    @Autowired
    private Updater updater;

    //Equals to amount of milliseconds in one day
    private static final long UPDATE_PERIOD = 1000 * 60 * 60 * 24;

    @Scheduled(fixedRate = UPDATE_PERIOD)
    public void run() {
        log.info("Starting shop parsing");
        updater.updateUniqueVinylsRewriteOffers();
        log.info("------------------- Finished shop parsing, successfully -------------------");
    }

}
