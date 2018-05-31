package com.mirza.app;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by yach0217 on 24.05.2018.
 */

@Configuration
public class EnvironmentConfiguration {

    static {
        ApiContextInitializer.init();
    }

    @Bean
    TelegramGameBot telegramGameBot(){
        return new TelegramGameBot();
    }

    @Bean
    TelegramSchedulerBot TelegramSchedulerBot(){
        return new TelegramSchedulerBot();
    }

    @Bean
    TelegramBotsApi telegramBotsApi(@Autowired TelegramGameBot telegramGameBot, @Autowired TelegramSchedulerBot telegramSchedulerBot) throws TelegramApiRequestException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(telegramGameBot);
        telegramBotsApi.registerBot(telegramSchedulerBot);
        return telegramBotsApi;
    }

    @Bean
    DataSource postgresDataSource() throws SQLException {
        PGSimpleDataSource dataSource =  new PGSimpleDataSource();
        dataSource.setServerName("localhost:3366/postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(@Autowired DataSource postgresDataSource) {
        return new JdbcTemplate(postgresDataSource);
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(@Autowired DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }
}