package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.DbTest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EntityScan(basePackages = "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb")
public class SecDbConfig {

    private static final AtomicReference<Properties> effectiveSecDbProperties = new AtomicReference<>(null);

    /**
     * Loads and merges properties for secdb, giving precedence to secdb.* properties.
     */
    public static Properties getEffectiveSecDbProperties() {
        if (effectiveSecDbProperties.get() == null) {
            Properties merged = DbTest.getMergedProperties();
            Properties props = new Properties();

            for (var key : merged.stringPropertyNames()) {
                if (key.startsWith("jakarta.persistence") || key.startsWith("hibernate.") || key.startsWith("hikari.")) {
                    props.setProperty(key, merged.getProperty(key));
                }
            }
            // Second pass: load secdb.* properties, stripping the prefix
            for (var key : merged.stringPropertyNames()) {
                if (key.startsWith("secdb.")) {
                    String strippedKey = key.substring("secdb.".length());
                    props.setProperty(strippedKey, merged.getProperty(key));
                }
            }

            // // Third pass: load any remaining properties from the merged properties
            // for (var key : merged.stringPropertyNames()) {
            //     if (!key.startsWith("jakarta.persistence") && !key.startsWith("hibernate.") && !key.startsWith("secdb.") && !key.startsWith("hikari.")) {
            //         if (!props.containsKey(key)) {
            //             props.setProperty(key, merged.getProperty(key));
            //         }
            //     }
            // }

            effectiveSecDbProperties.compareAndSet(null, props);
        }
        return effectiveSecDbProperties.get();
    }

    @Bean(name = "secDataSource")
    @PersistenceContext(unitName = "SecDbPU")
    public DataSource secDataSource() {
        Properties props = getEffectiveSecDbProperties();

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(props.getProperty("jakarta.persistence.jdbc.driver", "org.postgresql.Driver"));
        config.setJdbcUrl(props.getProperty("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/yourdb"));
        config.setUsername(props.getProperty("jakarta.persistence.jdbc.user", "postgres"));
        config.setPassword(props.getProperty("jakarta.persistence.jdbc.password", "pgpassword"));

        // Optional: set HikariCP-specific properties if present
        if (props.getProperty("hikari.maximumPoolSize") != null) {
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikari.maximumPoolSize")));
        }
        if (props.getProperty("hikari.minimumIdle") != null) {
            config.setMinimumIdle(Integer.parseInt(props.getProperty("hikari.minimumIdle")));
        }
        if (props.getProperty("hikari.poolName") != null) {
            config.setPoolName(props.getProperty("hikari.poolName"));
        }

        return new HikariDataSource(config);
    }

    @Bean(name = "secEntityManagerFactory")
    @PersistenceContext(unitName = "SecDbPU")
    public EntityManagerFactory createSecEntityManagerFactory(
        @Qualifier("secDataSource") DataSource secDataSource, Environment env) {
        // Build the effective properties for secdb
        Properties props = getEffectiveSecDbProperties();

        // If you want to inject the DataSource, you can set it as a property
        // (Hibernate supports this, but not all JPA providers do)
        // props.put("jakarta.persistence.nonJtaDataSource", secDataSource);

        // The persistence unit name must match the one in your persistence.xml, or you can use a dynamic unit
        String persistenceUnitName = "SecDbPU";

        // Create the EntityManagerFactory using the Jakarta Persistence API
        return Persistence.createEntityManagerFactory(persistenceUnitName, props);
    }
}