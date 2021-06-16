package com.vinylteam.vinyl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Starter {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Starter.class, args);

        //FIXME add security filter
        //FIXME add Updater
        //FIXME add Captcha (maybe exist in Spring)
        //FIXME investigate MailSender in Spring
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