package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
        String dbUrl = userProperties.getProperty("secdb.datasource.url");
        System.setProperty("secdb.datasource.jdbcUrl", dbUrl);
        String dbUser = userProperties.getProperty("secdb.datasource.username");
        String dbPassword = userProperties.getProperty("secdb.datasource.password");
        String dbSchema = userProperties.getProperty("secdb.datasource.schema");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(dbDriver);
//        config.setDriverClassName("org.postgresql.ds.PGSimpleDataSource");
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setSchema(dbSchema);
        config.setAutoCommit(false);
        config.setPoolName("SecDbPool");

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

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        em.setJpaProperties(jpaProperties);

        return em;
    }

    @Bean(name = "secTransactionManager")
    public JpaTransactionManager secTransactionManager(@Qualifier("secEntityManagerFactory") LocalContainerEntityManagerFactoryBean secEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(secEntityManagerFactory.getObject());
        return transactionManager;
    }
}
