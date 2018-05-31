package com.mirza.app;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * Created by yach0217 on 29.05.2018.
 */


@SpringBootApplication(scanBasePackages={"com.mirza"})
public class App implements CommandLineRunner {

    @Autowired
    TelegramBotsApi botsApi;

    public static void main( String[] args ) throws IOException, URISyntaxException, SQLException {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        ApiContextInitializer.init();
    }
}
