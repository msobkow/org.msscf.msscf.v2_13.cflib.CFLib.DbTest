package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import org.springframework.boot.context.properties.ConfigurationProperties;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class AppDbConfig {

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
        //String dbDriver = userProperties.getProperty("appdb.datasource.driver-class-name");
        String dbUrl = userProperties.getProperty("appdb.datasource.jdbc-url");
        // System.setProperty("appdb.datasource.jdbcUrl", dbUrl);
        String dbUser = userProperties.getProperty("appdb.datasource.username");
        String dbPassword = userProperties.getProperty("appdb.datasource.password");
        String dbSchema = userProperties.getProperty("appdb.datasource.hikari.schema");
        String hkPoolName = userProperties.getProperty("appdb.datasource.hikari.pool-name");

        // appHikariConfigMXBean.setUsername(dbUser);
        // appHikariConfigMXBean.setPassword(dbPassword);

        HikariConfig config = new HikariConfig();
        //config.setDriverClassName(dbDriver);
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

    @Bean(name = "appEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory(@Qualifier("appDataSource") DataSource appDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
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
        String dialect = userProperties.getProperty("appdb.jpa.properties.hibernate.dialect");
        String ddlAuto = userProperties.getProperty("appdb.jpa.hibernate.ddl-auto");

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", dialect);
        jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);
        em.setJpaProperties(jpaProperties);

        return em;
    }

    @Bean(name = "appTransactionManager")
    public JpaTransactionManager appTransactionManager(@Qualifier("appEntityManagerFactory") LocalContainerEntityManagerFactoryBean appEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
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

        SqlInitializationProperties props = new SqlInitializationProperties();
        props.setSchemaLocations(List.of(
            userProperties.getProperty("appdb.sql.schema-location", "classpath:db/appdb/schema.pgsql")));
        props.setDataLocations(List.of(
            userProperties.getProperty("appdb.sql.data-location", "classpath:db/appdb/data.pgsql")));
        props.setMode(DatabaseInitializationMode.valueOf(
            userProperties.getProperty("appdb.sql.init-mode", "ALWAYS")));
        props.setContinueOnError(Boolean.parseBoolean(
            userProperties.getProperty("appdb.sql.continue-on-error", "true")));
        
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
