package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.ResourceLoader;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import java.util.List;

@Configuration
@EnableTransactionManagement
public class SecDbConfig {

    @Bean(name = "secDataSource")
    @ConfigurationProperties(prefix = "secdb.datasource")
    public DataSource secDataSource() {
        // HikariConfig hikariConfig = new HikariConfig();
        // hikariConfig.setDriverClassName(System.getProperty("secdb.datasource.driver-class-name", "org.postgresql.Driver"));
        // hikariConfig.setJdbcUrl(System.getProperty("secdb.datasource.jdbc-url", "jdbc:postgresql://localhost:5432/secdb"));
        // hikariConfig.setUsername(System.getProperty("secdb.datasource.username", "postgres"));
        // hikariConfig.setPassword(System.getProperty("secdb.datasource.password", "pgpassword"));
        // hikariConfig.setSchema(System.getProperty("secdb.datasource.hikari.schema", "secdb"));
        // hikariConfig.setPoolName(System.getProperty("secdb.datasource.hikari.pool-name", "SecDbHikariCP"));

        // hikariConfig.setMaximumPoolSize(Integer.parseInt(System.getProperty("secdb.datasource.hikari.maximum-pool-size", "10")));
        // hikariConfig.setMinimumIdle(Integer.parseInt(System.getProperty("secdb.datasource.hikari.minimum-idle", "5")));
        // hikariConfig.setConnectionTimeout(Long.parseLong(System.getProperty("secdb.datasource.hikari.connection-timeout", "30000")));
        // hikariConfig.setIdleTimeout(Long.parseLong(System.getProperty("secdb.datasource.hikari.idle-timeout", "600000")));
        // hikariConfig.setMaxLifetime(Long.parseLong(System.getProperty("secdb.datasource.hikari.max-lifetime", "1800000")));
        // hikariConfig.setAutoCommit(Boolean.parseBoolean(System.getProperty("secdb.datasource.hikari.auto-commit", "true")));

        // return new HikariDataSource(hikariConfig);
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

   @Bean(name = "secEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secEntityManagerFactory(
            @Qualifier("secDataSource") DataSource secDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(secDataSource);
        em.setPackagesToScan(
            "org.msscf.msscf.v2_13.cflib.CFLib.dbutil",
            "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb"
        );
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setPersistenceUnitName("secPersistenceUnit");
        // Optionally, you can set JPA properties here if not using application.properties
        // Properties jpaProperties = new Properties();
        // jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        // jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        // em.setJpaProperties(jpaProperties);
        return em;
    }

    // @Bean(name = "secDbScriptInitializer")
    // public SqlDataSourceScriptDatabaseInitializer secDbScriptInitializer(
    //         @Qualifier("secDataSource") DataSource secDataSource,
    //         ResourceLoader resourceLoader) {

    //     SqlInitializationProperties props = new SqlInitializationProperties();
    //     props.setSchemaLocations(List.of(
    //         System.getProperty("secdb.sql.schema-location", "classpath:db/secdb/schema.pgsql")));
    //     props.setDataLocations(List.of(
    //         System.getProperty("secdb.sql.data-location", "classpath:db/secdb/data.pgsql")));
    //     props.setMode(DatabaseInitializationMode.valueOf(
    //         System.getProperty("secdb.sql.init-mode", "ALWAYS")));
    //     props.setContinueOnError(Boolean.parseBoolean(
    //         System.getProperty("secdb.sql.continue-on-error", "true")));
    //     props.setUsername(System.getProperty("secdb.sql.username", "postgres"));
    //     props.setPassword(System.getProperty("secdb.sql.password", "pgpassword"));
    //     return new SqlDataSourceScriptDatabaseInitializer(secDataSource, props);
    // }

    @Bean(name = "secTransactionManager")
    public PlatformTransactionManager secTransactionManager(
            @Qualifier("secEntityManagerFactory") EntityManagerFactory secEntityManagerFactory) {
        return new JpaTransactionManager(secEntityManagerFactory);
    }
}
