package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
public class AppDbConfig {

    @Primary
    @Bean(name = "appDataSource")
    @ConfigurationProperties(prefix = "appdb.datasource")
    public DataSource appDataSource() {
        Properties userProperties = new Properties();
        File userFile = new File(System.getProperty("user.home"), ".cfdbtest.properties");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                userProperties.load(fis);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        String dbDriver = userProperties.getProperty("appdb.datasource.driver-class-name");
        String dbUrl = userProperties.getProperty("appdb.datasource.url");
        System.setProperty("appdb.datasource.jdbcUrl", dbUrl);
        String dbUser = userProperties.getProperty("appdb.datasource.username");
        String dbPassword = userProperties.getProperty("appdb.datasource.password");
        String dbSchema = userProperties.getProperty("appdb.datasource.schema");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(dbDriver);
//        config.setDriverClassName("org.postgresql.ds.PGSimpleDataSource");
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setSchema(dbSchema);
        config.setAutoCommit(false);
        config.setPoolName("AppDbPool");

        HikariDataSource ds = new HikariDataSource(config);
        return ds;
        // return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "appEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory(@Qualifier("appDataSource") DataSource appDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(appDataSource);
        em.setPersistenceUnitName("appPersistenceUnit");
        em.setPackagesToScan("org.msscf.msscf.v2_13.cflib.CFLib.dbutil", "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        em.setJpaProperties(jpaProperties);

        return em;
    }

    @Primary
    @Bean(name = "appTransactionManager")
    public JpaTransactionManager appTransactionManager(@Qualifier("appEntityManagerFactory") LocalContainerEntityManagerFactoryBean appEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(appEntityManagerFactory.getObject());
        return transactionManager;
    }
}
