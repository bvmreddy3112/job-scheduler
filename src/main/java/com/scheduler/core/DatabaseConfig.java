package com.scheduler.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    
    private static HikariDataSource dataSource;

    public static DataSource getDataSource(){

        if(dataSource == null){

            com.zaxxer.hikari.HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/scheduler_db");
            config.setUsername("postgres");
            config.setPassword("kittu3112");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public static void close(){

        if (dataSource != null){
            dataSource.close();
        }
    }
}
