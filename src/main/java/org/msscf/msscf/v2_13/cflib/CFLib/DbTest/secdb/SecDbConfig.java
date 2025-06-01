package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.sql.DataSource;
import javax.xml.crypto.Data;

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
    @ConfigurationProperties(prefix = "secdb.datasource")
    public DataSource secDataSource() {
        Properties userProperties = new Properties();
        File userFile = new File(System.getProperty("user.home"), ".cfdbtest.properties");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                userProperties.load(fis);
                System.getProperties().putAll(userProperties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        Properties shortened = new Properties();
        for (String key : System.getProperties().stringPropertyNames()) {
            if (key.startsWith("secdb.")) {
                shortened.put(key.substring("secdb.".length()), System.getProperty(key));
            }
            else if (key.startsWith("spring.")) {
                shortened.put(key, System.getProperty(key));
            }
        }
        Properties noDataSource = new Properties();
        for (String key : shortened.stringPropertyNames()) {
            if (key.startsWith("datasource.")) {
                noDataSource.put(key.substring("datasource.".length()), System.getProperty(key));
            }
            else if (key.startsWith("spring.")) {
                noDataSource.put(key, shortened.getProperty(key));
            }
        }   
        // String dbDriver = System.getProperty("secdb.datasource.driver-class-name", "org.postgresql.Driver");
        // String dbUrl = System.getProperty("secdb.datasource.jdbc-url");
        // // System.setProperty("secdb.datasource.jdbcUrl", dbUrl);
        // String dbUser = System.getProperty("secdb.datasource.username");
        // String dbPassword = System.getProperty("secdb.datasource.password");
        // String dbSchema = System.getProperty("secdb.datasource.hikari.schema");
        // String hkPoolName = System.getProperty("secdb.datasource.hikari.pool-name");

        HikariConfig hikariConfig = new HikariConfig(noDataSource);
        // hikariConfig.setDriverClassName(dbDriver);
        // hikariConfig.setJdbcUrl(dbUrl);
        // hikariConfig.setUsername(dbUser);
        // hikariConfig.setPassword(dbPassword);
        // hikariConfig.setSchema(dbSchema);
        // hikariConfig.setAutoCommit(true);
        // hikariConfig.setPoolName(hkPoolName);
        // Uncomment the following lines to set additional HikariCP properties
        // hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
        // hikariConfig.setMinimumIdle(config.getMinimumIdle());
        // hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
        // hikariConfig.setIdleTimeout(config.getIdleTimeout());
        // hikariConfig.setMaxLifetime(config.getMaxLifetime());
        // hikariConfig.setLeakDetectionThreshold(config.getLeakDetectionThreshold());
        // hikariConfig.setInitializationFailTimeout(config.getInitializationFailTimeout());
        // hikariConfig.setConnectionTestQuery(config.getConnectionTestQuery());
        // hikariConfig.setConnectionInitSql(config.getConnectionInitSql());
        // hikariConfig.setDataSourceClassName(config.getDataSourceClassName());
        // hikariConfig.setDriverClassName(config.getDriverClassName());
        // hikariConfig.setCatalog(config.getCatalog());
        // hikariConfig.setTransactionIsolation(config.getTransactionIsolation());
        // hikariConfig.setValidationTimeout(config.getValidationTimeout());

        // Create and return the HikariDataSource
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(hikariConfig.getDriverClassName());
        ds.setJdbcUrl(hikariConfig.getJdbcUrl());
        ds.setUsername(hikariConfig.getUsername());
        ds.setPassword(hikariConfig.getPassword());
        ds.setSchema(hikariConfig.getSchema());
        ds.setAutoCommit(hikariConfig.isAutoCommit());
        ds.setPoolName(hikariConfig.getPoolName());
        ds.setMaximumPoolSize(hikariConfig.getMaximumPoolSize());
        ds.setMinimumIdle(hikariConfig.getMinimumIdle());
        ds.setConnectionTimeout(hikariConfig.getConnectionTimeout());
        ds.setIdleTimeout(hikariConfig.getIdleTimeout());
        ds.setMaxLifetime(hikariConfig.getMaxLifetime());
        ds.setLeakDetectionThreshold(hikariConfig.getLeakDetectionThreshold());
        ds.setInitializationFailTimeout(hikariConfig.getInitializationFailTimeout());
        ds.setConnectionTestQuery(hikariConfig.getConnectionTestQuery());
        ds.setConnectionInitSql(hikariConfig.getConnectionInitSql());
        ds.setDataSourceClassName(hikariConfig.getDataSourceClassName());
        ds.setDriverClassName(hikariConfig.getDriverClassName());
        ds.setCatalog(hikariConfig.getCatalog());
        ds.setTransactionIsolation(hikariConfig.getTransactionIsolation());
        ds.setValidationTimeout(hikariConfig.getValidationTimeout());
        return ds;
        // return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "secEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secEntityManagerFactory(@Qualifier("secDataSource") DataSource secDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(secDataSource);
        em.setPersistenceUnitName("secPersistenceUnit");
        em.setPackagesToScan("org.msscf.msscf.v2_13.cflib.CFLib.dbutil", "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties userProperties = new Properties();
        File userFile = new File(System.getProperty("user.home"), ".cfdbtest.properties");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                userProperties.load(fis);
                System.getProperties().putAll(userProperties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        String dialect = System.getProperty("secdb.jpa.properties.hibernate.dialect");
        String ddlAuto = System.getProperty("secdb.jpa.hibernate.ddl-auto");

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
                System.getProperties().putAll(userProperties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }

        SqlInitializationProperties props = new SqlInitializationProperties();
        props.setSchemaLocations(List.of(
            System.getProperty("secdb.sql.schema-location", "classpath:db/secdb/schema.pgsql")));
        props.setDataLocations(List.of(
            System.getProperty("secdb.sql.data-location", "classpath:db/secdb/data.pgsql")));
        props.setMode(DatabaseInitializationMode.valueOf(
            System.getProperty("secdb.sql.init-mode", "ALWAYS")));
        props.setContinueOnError(Boolean.parseBoolean(
            System.getProperty("secdb.sql.continue-on-error", "true")));
        
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
