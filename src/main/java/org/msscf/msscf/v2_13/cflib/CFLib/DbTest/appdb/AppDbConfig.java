package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.core.io.ResourceLoader;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class AppDbConfig {

    @Bean(name = "appDataSource")
    public DataSource appDataSource() {
        Properties userProperties = new Properties();
        File userFile = new File(System.getProperty("user.home"), ".cfdbtest.properties");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                userProperties.load(fis);
                // System.getProperties().putAll(userProperties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        else {
            throw new RuntimeException("No user properties file found at " + userFile.getAbsolutePath());
        }
        Properties merged = new Properties();
        merged.putAll(System.getProperties());
        merged.putAll(userProperties);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(merged.getProperty("appdb.datasource.driver-class-name", "org.postgresql.Driver"));
        hikariConfig.setJdbcUrl(merged.getProperty("appdb.datasource.jdbc-url"));
        hikariConfig.setUsername(merged.getProperty("appdb.datasource.username"));
        hikariConfig.setPassword(merged.getProperty("appdb.datasource.password"));
        hikariConfig.setSchema(merged.getProperty("appdb.datasource.hikari.schema"));
        hikariConfig.setPoolName(merged.getProperty("appdb.datasource.hikari.pool-name"));

        hikariConfig.setMaximumPoolSize(Integer.parseInt(merged.getProperty("appdb.datasource.hikari.maximum-pool-size", "10")));
        hikariConfig.setMinimumIdle(Integer.parseInt(merged.getProperty("appdb.datasource.hikari.minimum-idle", "5")));
        hikariConfig.setConnectionTimeout(Long.parseLong(merged.getProperty("appdb.datasource.hikari.connection-timeout", "30000")));
        hikariConfig.setIdleTimeout(Long.parseLong(merged.getProperty("appdb.datasource.hikari.idle-timeout", "600000")));
        hikariConfig.setMaxLifetime(Long.parseLong(merged.getProperty("appdb.datasource.hikari.max-lifetime", "1800000")));
        hikariConfig.setAutoCommit(Boolean.parseBoolean(merged.getProperty("appdb.datasource.hikari.auto-commit", "true")));

        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "appEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory(@Qualifier("appDataSource") DataSource appDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        if (appDataSource == null) {
            throw new IllegalArgumentException("appDataSource must not be null");
        }
        em.setDataSource(appDataSource);
        em.setPersistenceUnitName("appPersistenceUnit");
        em.setPackagesToScan("org.msscf.msscf.v2_13.cflib.CFLib.dbutil", "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties userProperties = new Properties();
        File userFile = new File(System.getProperty("user.home"), ".cfdbtest.properties");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                userProperties.load(fis);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        else {
            throw new RuntimeException("No user properties file found at " + userFile.getAbsolutePath());
        }
        Properties merged = new Properties();
        merged.putAll(System.getProperties());
        merged.putAll(userProperties);

        String dialect = merged.getProperty("appdb.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        String ddlAuto = merged.getProperty("appdb.jpa.hibernate.ddl-auto", "create");

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", dialect);
        jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);
        em.setJpaProperties(jpaProperties);
        return em;
    }

    @Bean(name = "appTransactionManager")
    public JpaTransactionManager appTransactionManager(@Qualifier("appEntityManagerFactory") LocalContainerEntityManagerFactoryBean appEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        if (appEntityManagerFactory == null) {
            throw new IllegalArgumentException("appEntityManagerFactory must not be null");
        }
        if (appEntityManagerFactory.getObject() == null) {
            throw new IllegalStateException("appEntityManagerFactory EntityManagerFactory object is null");
        }
        transactionManager.setEntityManagerFactory(appEntityManagerFactory.getObject());
        return transactionManager;
    }

    @Bean(name = "appDbScriptInitializer")
    public SqlDataSourceScriptDatabaseInitializer appDbScriptInitializer(
            @Qualifier("appDataSource") DataSource appDataSource,
            ResourceLoader resourceLoader) {
        
        Properties userProperties = new Properties();
        File userFile = new File(System.getProperty("user.home"), ".cfdbtest.properties");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                userProperties.load(fis);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        else {
            throw new RuntimeException("No user properties file found at " + userFile.getAbsolutePath());
        }
        Properties merged = new Properties();
        merged.putAll(System.getProperties());
        merged.putAll(userProperties);

        SqlInitializationProperties props = new SqlInitializationProperties();
        props.setSchemaLocations(List.of(
            merged.getProperty("appdb.sql.schema-location", "classpath:db/appdb/schema.pgsql")));
        props.setDataLocations(List.of(
            merged.getProperty("appdb.sql.data-location", "classpath:db/appdb/data.pgsql")));
        props.setMode(DatabaseInitializationMode.valueOf(
            merged.getProperty("appdb.sql.init-mode", "ALWAYS")));
        props.setContinueOnError(Boolean.parseBoolean(
            merged.getProperty("appdb.sql.continue-on-error", "true")));
        props.setUsername(System.getProperty("appdb.sql.username", "postgres"));
        props.setPassword(System.getProperty("appdb.sql.password", "pgpassword"));
        return new SqlDataSourceScriptDatabaseInitializer(appDataSource, props);
    }

    @Bean(name = "appHikariConfigMXBean")
    public HikariConfigMXBean appHikariConfigMXBean(@Qualifier("appDataSource") DataSource appDataSource) {
        if (appDataSource instanceof HikariDataSource) {
            return ((HikariDataSource) appDataSource).getHikariConfigMXBean();
        }
        throw new IllegalStateException("DataSource is not a HikariDataSource");
    }
}
