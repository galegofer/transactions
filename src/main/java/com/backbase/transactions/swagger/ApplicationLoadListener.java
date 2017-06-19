package com.backbase.transactions.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Application startup listener, to perform actions when the application just
 * load, in this case, just to show up the ASCII banner.
 * 
 * @author User
 */
public class ApplicationLoadListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final String BANNER = "\r\n  _______                             _   _                 \r\n |__   __|                           | | (_)                \r\n    | |_ __ __ _ _ __  ___  __ _  ___| |_ _  ___  _ __  ___ \r\n    | | '__/ _` | '_ \\/ __|/ _` |/ __| __| |/ _ \\| '_ \\/ __|\r\n    | | | | (_| | | | \\__ \\ (_| | (__| |_| | (_) | | | \\__ \\\r\n    |_|_|  \\__,_|_| |_|___/\\__,_|\\___|\\__|_|\\___/|_| |_|___/";
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLoadListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info(BANNER);
    }
}
