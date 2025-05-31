package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.springframework.boot.context.properties.ConfigurationProperties;
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
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class SecDbConfig {

    @Bean(name = "secDataSource")
    @ConfigurationProperties(prefix = "secdb.datasource")
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
        String dbDriver = userProperties.getProperty("secdb.datasource.driver-class-name");
        String dbUrl = userProperties.getProperty("secdb.datasource.jdbc-url");
        // System.setProperty("secdb.datasource.jdbcUrl", dbUrl);
        String dbUser = userProperties.getProperty("secdb.datasource.username");
        String dbPassword = userProperties.getProperty("secdb.datasource.password");
        String dbSchema = userProperties.getProperty("secdb.datasource.hikari.schema");
        String hkPoolName = userProperties.getProperty("secdb.datasource.pool-name");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(dbDriver);
//        config.setDriverClassName("org.postgresql.ds.PGSimpleDataSource");
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setSchema(dbSchema);
        config.setAutoCommit(false);
        config.setPoolName(hkPoolName);

        HikariDataSource ds = new HikariDataSource(config);
        return ds;
        // return DataSourceBuilder.create().build();
    }

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
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        String dialect = userProperties.getProperty("secdb.jpa.properties.hibernate.dialect");
        String ddlAuto = userProperties.getProperty("secdb.jpa.hibernate.ddl-auto");

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", dialect);
        jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);
        em.setJpaProperties(jpaProperties);
        return em;
    }

    @Bean(name = "secTransactionManager")
    public JpaTransactionManager secTransactionManager(@Qualifier("secEntityManagerFactory") LocalContainerEntityManagerFactoryBean secEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(secEntityManagerFactory.getObject());
        return transactionManager;
    }

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

        SqlInitializationProperties props = new SqlInitializationProperties();
        props.setSchemaLocations(List.of(
            userProperties.getProperty("secdb.sql.schema-location", "classpath:db/secdb/schema.sql")));
        props.setDataLocations(List.of(
            userProperties.getProperty("secdb.sql.data-location", "classpath:db/secdb/data.sql")));
        props.setMode(DatabaseInitializationMode.valueOf(
            userProperties.getProperty("secdb.sql.init-mode", "ALWAYS")));
        props.setContinueOnError(Boolean.parseBoolean(
            userProperties.getProperty("secdb.sql.continue-on-error", "true")));
        
        return new SqlDataSourceScriptDatabaseInitializer(secDataSource, props);
    }
}
