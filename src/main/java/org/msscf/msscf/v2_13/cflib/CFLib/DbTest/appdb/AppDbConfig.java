package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

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
    @ConfigurationProperties(prefix = "appdb.datasource")
    public DataSource appDataSource() {
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

        String dbDriver = System.getProperty("appdb.datasource.driver-class-name");
        String dbUrl = System.getProperty("appdb.datasource.jdbc-url");
        // System.setProperty("appdb.datasource.jdbcUrl", dbUrl);
        String dbUser = System.getProperty("appdb.datasource.username");
        String dbPassword = System.getProperty("appdb.datasource.password");
        String dbSchema = System.getProperty("appdb.datasource.hikari.schema");
        String hkPoolName = System.getProperty("appdb.datasource.hikari.pool-name");

        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(dbDriver);
        ds.setJdbcUrl(dbUrl);
        ds.setUsername(dbUser);
        ds.setPassword(dbPassword);
        ds.setSchema(dbSchema);
        ds.setAutoCommit(true);
        ds.setPoolName(hkPoolName);
        // ds.setMaximumPoolSize(config.getMaximumPoolSize());
        // ds.setMinimumIdle(config.getMinimumIdle());
        // ds.setConnectionTimeout(config.getConnectionTimeout());
        // ds.setIdleTimeout(config.getIdleTimeout());
        // ds.setMaxLifetime(config.getMaxLifetime());
        // ds.setLeakDetectionThreshold(config.getLeakDetectionThreshold());
        // ds.setInitializationFailTimeout(config.getInitializationFailTimeout());
        // ds.setConnectionTestQuery(config.getConnectionTestQuery());
        // ds.setConnectionInitSql(config.getConnectionInitSql());
        // ds.setDataSourceClassName(config.getDataSourceClassName());
        // ds.setDriverClassName(config.getDriverClassName());
        // ds.setCatalog(config.getCatalog());
        // ds.setTransactionIsolation(config.getTransactionIsolation());
        // ds.setValidationTimeout(config.getValidationTimeout());
        return ds;
        // return DataSourceBuilder.create().build();
    }

    @Bean(name = "appEntityManagerFactory")
    @ConfigurationProperties(prefix = "appdb.datasource")
    public EntityManagerFactory appEntityManagerFactory(@Qualifier("appDataSource") DataSource appDataSource) {
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
                System.getProperties().putAll(userProperties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        String dialect = System.getProperty("appdb.jpa.properties.hibernate.dialect");
        String ddlAuto = System.getProperty("appdb.jpa.hibernate.ddl-auto");

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", dialect);
        jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);
        em.setJpaProperties(jpaProperties);

        return em.getNativeEntityManagerFactory();
    }

    @Bean(name = "appTransactionManager")
    public JpaTransactionManager appTransactionManager(@Qualifier("appEntityManagerFactory") EntityManagerFactory appEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(appEntityManagerFactory);
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
                System.getProperties().putAll(userProperties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }

        SqlInitializationProperties props = new SqlInitializationProperties();
        props.setSchemaLocations(List.of(
            System.getProperty("appdb.sql.schema-location", "classpath:db/appdb/schema.pgsql")));
        props.setDataLocations(List.of(
            System.getProperty("appdb.sql.data-location", "classpath:db/appdb/data.pgsql")));
        props.setMode(DatabaseInitializationMode.valueOf(
            System.getProperty("appdb.sql.init-mode", "ALWAYS")));
        props.setContinueOnError(Boolean.parseBoolean(
            System.getProperty("appdb.sql.continue-on-error", "true")));
        
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
