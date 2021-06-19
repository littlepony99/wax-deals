package com.vinylteam.vinyl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySource({"classpath:application.properties", "classpath:dev.application.properties"})
public class Starter {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Starter.class, args);

        //FIXME fixed application.properties
        //FIXME fix if discogs userName is null then is problem in db
        //FIXME add security filter
        //FIXME look on exception into ConfirmationController
        //FIXME if recovery token is not correct we must don't show password form ??? - HOW WORK MESSENGER

        //FIXME DefaultErrorHandler

        //FIXME add tests
        //FIXME add Updater
        //FIXME Testing all script with send mail and add to db


    /*    TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                log.info("Started updater task");
                updater.updateUniqueVinylsRewriteOffers();
                log.info("Finished updater task");
            }
        };

        Timer updateTimer = new Timer("Update Timer");
        long updatePeriod = Long.parseLong(propertiesReader.getProperty("updatePeriod"));
        updateTimer.scheduleAtFixedRate(updateTask, 0, updatePeriod);
*/
    }
}