package com.vinylteam.vinyl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.dao.*;
import com.vinylteam.vinyl.dao.jdbc.*;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.*;
import com.vinylteam.vinyl.service.impl.*;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.util.PropertiesReader;
import com.vinylteam.vinyl.util.RawOffersSorter;
import com.vinylteam.vinyl.util.Updater;
import com.vinylteam.vinyl.util.impl.*;
import com.vinylteam.vinyl.web.filter.SecurityFilter;
import com.vinylteam.vinyl.web.handler.DefaultErrorHandler;
import com.vinylteam.vinyl.web.servlets.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.JarFileResource;
import org.eclipse.jetty.util.resource.Resource;

import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class Starter {

    private static final PropertiesReader propertiesReader = new PropertiesReader();
    private static final String RESOURCE_PATH = propertiesReader.getProperty("resource.path");

    public static void main(String[] args) throws Exception {
        DiscogsService discogsService = new DefaultDiscogsService(
                propertiesReader.getProperty("consumer.key"),
                propertiesReader.getProperty("consumer.secret"),
                propertiesReader.getProperty("user.agent"),
                propertiesReader.getProperty("callback.url"), new ObjectMapper()
        );

//DAO
        HikariDataSource dataSource;
        HikariConfig config = new HikariConfig();
        PropertiesReader propertiesReader = new PropertiesReader();
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        log.info("Configured dataSource");

        UserDao userDao = new JdbcUserDao(dataSource);
        UniqueVinylDao uniqueVinylDao = new JdbcUniqueVinylDao(dataSource);
        OfferDao offerDao = new JdbcOfferDao(dataSource);
        ShopDao shopDao = new JdbcShopDao(dataSource);
        UserPostDao userPostDao = new JdbcUserPostDao(dataSource);
        RecoveryPasswordDao recoveryPasswordDao = new JdbcRecoveryPasswordDao(dataSource);

//SERVICE
        SecurityService securityService = new DefaultSecurityService();
        UserService userService = new DefaultUserService(userDao, securityService);
        UniqueVinylService uniqueVinylService = new DefaultUniqueVinylService(uniqueVinylDao);
        OfferService offerService = new DefaultOfferService(offerDao);
        ShopService shopService = new DefaultShopService(shopDao);
        MailSender mailSender = new MailSender(propertiesReader.getProperty("mail.smtp.username"),
                propertiesReader.getProperty("mail.smtp.password"),
                propertiesReader.getProperty("mail.smtp.host"),
                propertiesReader.getProperty("mail.smtp.port"),
                propertiesReader.getProperty("mail.smtp.auth"));
        UserPostService userPostService = new DefaultUserPostService(userPostDao, mailSender);
        CaptchaService defaultCaptchaService = new DefaultCaptchaService();
        RecoveryPasswordService recoveryPasswordService = new DefaultRecoveryPasswordService(recoveryPasswordDao, userService);
//UTIL, FILL IN DATABASE
        ShopsParser shopsParser = new ShopsParser();
        RawOffersSorter rawOffersSorter = new RawOffersSorter();
        List<VinylParser> vinylParsers = List.of(new VinylUaParser(), new JunoVinylParser(), new DecksParser(), new CloneNlParser());
        ParserHolder parserHolder = new ParserHolder(vinylParsers);
        Updater updater = new Updater(uniqueVinylService, offerService, shopsParser, vinylParsers, rawOffersSorter);
        TimerTask updateTask = new TimerTask() {
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

//WEB
        SecurityFilter securityFilter = new SecurityFilter();
        SignInServlet signInServlet = new SignInServlet(userService);
        SignUpServlet signUpServlet = new SignUpServlet(userService);
        CatalogueServlet catalogueServlet = new CatalogueServlet(uniqueVinylService, discogsService);
        SearchResultsServlet searchResultsServlet = new SearchResultsServlet(uniqueVinylService);
        OneVinylOffersServlet oneVinylOffersServlet = new OneVinylOffersServlet(uniqueVinylService, offerService, shopService, discogsService, parserHolder);
        SignOutServlet signOutServlet = new SignOutServlet();
        ProfileServlet profileServlet = new ProfileServlet();
        ShopServlet shopServlet = new ShopServlet(shopService);
        EditProfileServlet editProfileServlet = new EditProfileServlet(securityService, userService);
        DeleteProfileServlet deleteProfileServlet = new DeleteProfileServlet(userService);
        HomeServlet homeServlet = new HomeServlet();
        ContactUsServlet contactUsServlet = new ContactUsServlet(userPostService, defaultCaptchaService);
        ImageCaptchaServlet imageCaptchaServlet = new ImageCaptchaServlet();
        AboutServlet aboutServlet = new AboutServlet();
        RecoveryPasswordServlet recoveryPasswordServlet = new RecoveryPasswordServlet(recoveryPasswordService, mailSender);
        ChangePasswordServlet changePasswordServlet = new ChangePasswordServlet(recoveryPasswordService);

        Resource resource = JarFileResource.newClassPathResource(RESOURCE_PATH);
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setErrorHandler(new DefaultErrorHandler());
        servletContextHandler.setBaseResource(resource);

        servletContextHandler.addFilter(new FilterHolder(securityFilter), "/*",
                EnumSet.of(DispatcherType.REQUEST));
        servletContextHandler.addServlet(new ServletHolder(signInServlet), "/signIn");
        servletContextHandler.addServlet(new ServletHolder(signUpServlet), "/signUp");
        servletContextHandler.addServlet(new ServletHolder(catalogueServlet), "/catalog");
        servletContextHandler.addServlet(new ServletHolder(searchResultsServlet), "/search");
        servletContextHandler.addServlet(new ServletHolder(oneVinylOffersServlet), "/oneVinyl");
        servletContextHandler.addServlet(new ServletHolder(signOutServlet), "/signOut");
        servletContextHandler.addServlet(new ServletHolder(profileServlet), "/profile");
        servletContextHandler.addServlet(new ServletHolder(shopServlet), "/stores");
        servletContextHandler.addServlet(new ServletHolder(editProfileServlet), "/editProfile");
        servletContextHandler.addServlet(new ServletHolder(deleteProfileServlet), "/deleteProfile");
        servletContextHandler.addServlet(new ServletHolder(homeServlet), "");
        servletContextHandler.addServlet(new ServletHolder(contactUsServlet), "/contact");
        servletContextHandler.addServlet(new ServletHolder(imageCaptchaServlet), "/captcha");
        servletContextHandler.addServlet(new ServletHolder(aboutServlet), "/about");
        servletContextHandler.addServlet(new ServletHolder(recoveryPasswordServlet), "/recoveryPassword");
        servletContextHandler.addServlet(new ServletHolder(changePasswordServlet), "/newPassword");

        servletContextHandler.addServlet(DefaultServlet.class, "/*");

        Server server = new Server(Integer.parseInt(propertiesReader.getProperty("appPort")));
        server.setHandler(servletContextHandler);
        server.start();
        log.info("Server started");
    }

}