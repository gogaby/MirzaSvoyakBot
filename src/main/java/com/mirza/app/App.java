package com.mirza.app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * Created by yach0217 on 29.05.2018.
 */


@SpringBootApplication(scanBasePackages={"com.mirza"})
public class App{

    public static void main( String[] args ) throws IOException, URISyntaxException, SQLException {
        SpringApplication.run(App.class, args);
    }

}
