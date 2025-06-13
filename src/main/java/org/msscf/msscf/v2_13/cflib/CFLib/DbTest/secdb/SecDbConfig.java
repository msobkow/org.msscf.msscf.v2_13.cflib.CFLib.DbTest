package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
public class SecDbConfig {

    @Primary
    @Bean(name = "secDataSource")
    public DataSource secDataSource() {
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

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(merged.getProperty("secdb.datasource.driver-class-name", "org.postgresql.Driver"));
        hikariConfig.setJdbcUrl(merged.getProperty("secdb.datasource.jdbc-url"));
        hikariConfig.setUsername(merged.getProperty("secdb.datasource.username"));
        hikariConfig.setPassword(merged.getProperty("secdb.datasource.password"));
        hikariConfig.setSchema(merged.getProperty("secdb.datasource.hikari.schema"));
        hikariConfig.setPoolName(merged.getProperty("secdb.datasource.hikari.pool-name"));

        hikariConfig.setMaximumPoolSize(Integer.parseInt(merged.getProperty("secdb.datasource.hikari.maximum-pool-size", "10")));
        hikariConfig.setMinimumIdle(Integer.parseInt(merged.getProperty("secdb.datasource.hikari.minimum-idle", "5")));
        hikariConfig.setConnectionTimeout(Long.parseLong(merged.getProperty("secdb.datasource.hikari.connection-timeout", "30000")));
        hikariConfig.setIdleTimeout(Long.parseLong(merged.getProperty("secdb.datasource.hikari.idle-timeout", "600000")));
        hikariConfig.setMaxLifetime(Long.parseLong(merged.getProperty("secdb.datasource.hikari.max-lifetime", "1800000")));
        hikariConfig.setAutoCommit(Boolean.parseBoolean(merged.getProperty("secdb.datasource.hikari.auto-commit", "true")));

        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name = "secEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secEntityManagerFactory(@Qualifier("secDataSource") DataSource secDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        if (secDataSource == null) {
            throw new IllegalArgumentException("secDataSource must not be null");
        }
        em.setDataSource(secDataSource);
        em.setPersistenceUnitName("secPersistenceUnit");
        em.setPackagesToScan("org.msscf.msscf.v2_13.cflib.CFLib.dbutil", "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb");
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

        String dialect = merged.getProperty("secdb.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        String ddlAuto = merged.getProperty("secdb.jpa.hibernate.ddl-auto", "create");

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", dialect);
        jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);
        em.setJpaProperties(jpaProperties);
        return em;
    }

    @Primary
    @Bean(name = "secTransactionManager")
    public JpaTransactionManager secTransactionManager(@Qualifier("secEntityManagerFactory") LocalContainerEntityManagerFactoryBean secEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        if (secEntityManagerFactory == null) {
            throw new IllegalArgumentException("secEntityManagerFactory must not be null");
        }
        if (secEntityManagerFactory.getObject() == null) {
            throw new IllegalStateException("secEntityManagerFactory EntityManagerFactory object is null");
        }
        transactionManager.setEntityManagerFactory(secEntityManagerFactory.getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = "secDbScriptInitializer")
    public SqlDataSourceScriptDatabaseInitializer secDbScriptInitializer(
            @Qualifier("secDataSource") DataSource secDataSource,
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
            merged.getProperty("secdb.sql.schema-location", "classpath:db/secdb/schema.pgsql")));
        props.setDataLocations(List.of(
            merged.getProperty("secdb.sql.data-location", "classpath:db/secdb/data.pgsql")));
        props.setMode(DatabaseInitializationMode.valueOf(
            merged.getProperty("secdb.sql.init-mode", "ALWAYS")));
        props.setContinueOnError(Boolean.parseBoolean(
            merged.getProperty("secdb.sql.continue-on-error", "true")));
        props.setUsername(System.getProperty("secdb.sql.username", "postgres"));
        props.setPassword(System.getProperty("secdb.sql.password", "pgpassword"));
        return new SqlDataSourceScriptDatabaseInitializer(secDataSource, props);
    }

    @Primary
    @Bean(name = "secHikariConfigMXBean")
    public HikariConfigMXBean secHikariConfigMXBean(@Qualifier("secDataSource") DataSource secDataSource) {
        if (secDataSource instanceof HikariDataSource) {
            return ((HikariDataSource) secDataSource).getHikariConfigMXBean();
        }
        throw new IllegalStateException("DataSource is not a HikariDataSource");
    }
}
