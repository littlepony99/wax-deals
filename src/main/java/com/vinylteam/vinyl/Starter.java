package com.vinylteam.vinyl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Starter {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Starter.class, args);

        //FIXME add Captcha (maybe exist in Spring)
        //FIXME investigate MailSender in Spring
        //FIXME DefaultErrorHandler
        //FIXME fix if discogs userName is null then is problem in db
        //FIXME if user was added and mail wasn't sent then user receive message that WaxDeals couldn't create profile

        //FIXME if recovery token is not correct we must don't show password form ??? - HOW WORK MESSENGER
        //FIXME add security filter
        //FIXME add Updater
        //FIXME add tests
        //FIXME clean and tidy project


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
//WEB
        /*SecurityFilter securityFilter = new SecurityFilter();*/

      /*  servletContextHandler.addFilter(new FilterHolder(securityFilter), "/*",
                EnumSet.of(DispatcherType.REQUEST));*/
    }
}