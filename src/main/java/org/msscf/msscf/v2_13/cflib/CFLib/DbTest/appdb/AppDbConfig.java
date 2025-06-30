package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import javax.sql.DataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.DbTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EntityScan(basePackages = "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb")
public class AppDbConfig {

    public final static String persistenceUnitName = "AppDbPU";

    private static final AtomicReference<DataSource> refAppDataSource = new AtomicReference<>(null);
    private static final AtomicReference<Properties> appEntityManagerFactoryProperties = new AtomicReference<>(null);
    private static final AtomicReference<EntityManagerFactory> refAppEntityManagerFactory = new AtomicReference<>(null);

    @Autowired
    @Qualifier("appEntityManagerFactory")
    private static EntityManagerFactory emf;

    @Bean(name = "appDataSource")
    @PersistenceContext(unitName = "AppDbPU")
    public DataSource appDataSource() {
        if (refAppDataSource.get() == null) {
            Properties props = DbTest.getMergedProperties();

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(props.getProperty("appdb.jakarta.persistence.jdbc.driver", props.getProperty("jakarta.persistence.jdbc.driver", "org.postgresql.Driver")));
            config.setJdbcUrl(props.getProperty("appdb.jakarta.persistence.jdbc.url", props.getProperty("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/yourdb")));
            config.setUsername(props.getProperty("appdb.jakarta.persistence.jdbc.user", props.getProperty("jakarta.persistence.jdbc.user", "postgres")));
            config.setPassword(props.getProperty("appdb.jakarta.persistence.jdbc.password", props.getProperty("jakarta.persistence.jdbc.password", "pgpassword")));

            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("appdb.hikari.maximumPoolSize", props.getProperty("hikari.maximumPoolSize", "10"))));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("appdb.hikari.minimumIdle", props.getProperty("hikari.minimumIdle", "5"))));
            config.setPoolName(props.getProperty("appdb.hikari.poolName", props.getProperty("hikari.poolName", "AppDbHikariCP")));
            config.setAutoCommit(Boolean.getBoolean(props.getProperty("appdb.hikari.auto-commit", props.getProperty("hikari.auto-commit", "true"))));

            DataSource ds = new HikariDataSource(config);
            refAppDataSource.compareAndSet(null, ds);
        }
        return refAppDataSource.get();
    }

    public static Properties getAppEntityManagerFactoryProperties() {
        if (appEntityManagerFactoryProperties.get() == null) {
            // Build the effective properties for secdb
            // The persistence unit name must match the one in your persistence.xml, or you can use a dynamic unit
            Properties merged = DbTest.getMergedProperties();
            String jakartaPersistenceJdbcDriver = merged.getProperty("appdb.jakarta.persistence.jdbc.driver", merged.getProperty("jakarta.persistence.jdbc.driver", null));
            String jakartaPersistenceJdbcUrl = merged.getProperty("appdb.jakarta.persistence.jdbc.url", merged.getProperty("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/dbtestdb"));
            String jakartaPersistenceJdbcUser = merged.getProperty("appdb.jakarta.persistence.jdbc.user", merged.getProperty("jakarta.persistence.jdbc.user", "postgres"));
            String jakartaPersistenceJdbcPassword = merged.getProperty("appdb.jakarta.persistence.jdbc.password", merged.getProperty("jakarta.persistence.jdbc.password", "pgpassword"));
            String jakartaPersistenceSchemaGenerationDatabaseAction = merged.getProperty("appdb.jakarta.persistence.schema-generation.database.action", merged.getProperty("jakarta.persistence.schema-generation.database.action", null));
            String jakartaPersistenceSchemaGenerationScriptsAction = merged.getProperty("appdb.jakarta.persistence.schema-generation.scripts.action", merged.getProperty("jakarta.persistence.schema-generation.scripts.action", null));
            String jakartaPersistenceSchemaGenerationCreateSource = merged.getProperty("appdb.jakarta.persistence.schema-generation.create-source", merged.getProperty("jakarta.persistence.schema-generation.create-source", "metadata"));
            String jakartaPersistenceSchemaGenerationDropSource = merged.getProperty("appdb.jakarta.persistence.schema-generation.drop-source", merged.getProperty("jakarta.persistence.schema-generation.drop-source", "metadata"));
            String jakartaPersistenceCreateDatabaseSchemas = merged.getProperty("appdb.jakarta.persistence.create-database-schemas", merged.getProperty("jakarta.persistence.create-database-schemas", "true"));
            String jakartaNonJtaDataSource = merged.getProperty("appdb.jakarta.persistence.nonJtaDataSource", merged.getProperty("jakarta.persistence.nonJtaDataSource", null));
            String jakartaJtaDataSource = merged.getProperty("appdb.jakarta.persistence.jtaDataSource", merged.getProperty("jakarta.persistence.jtaDataSource", null));
            String hibernateDialect = merged.getProperty("appdb.hibernate.dialect", merged.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
            String hibernateHbm2ddlAuto = merged.getProperty("appdb.hibernate.hbm2ddl.auto", merged.getProperty("hibernate.hbm2ddl.auto", "update"));
            String hibernateShowSql = merged.getProperty("appdb.hibernate.show_sql", merged.getProperty("hibernate.show_sql", "false"));
            String hibernateFormatSql = merged.getProperty("appdb.hibernate.format_sql", merged.getProperty("hibernate.format_sql", "false"));
            String hibernateConnectionPoolSize = merged.getProperty("appdb.hibernate.connection_pool_size", merged.getProperty("hibernate.connection_pool_size", "10"));
            String hibernateConnectionDatasource = merged.getProperty("appdb.hibernate.connection_datasource", merged.getProperty("hibernate.connection_datasource", null));
            String hibernateCacheRegionFactoryClass = merged.getProperty("appdb.hibernate.cache.region.factory_class", merged.getProperty("hibernate.cache.region.factory_class", null));
            String hibernateDefaultSchema = merged.getProperty("appdb.hibernate.default_schema", "appdb");

            Properties applicable = new Properties();
            if (persistenceUnitName != null && !persistenceUnitName.isEmpty()) {
                applicable.setProperty("jakarta.persistence.unitName", persistenceUnitName);
            }
            if (jakartaPersistenceJdbcDriver != null && !jakartaPersistenceJdbcDriver.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.driver", jakartaPersistenceJdbcDriver);
            }
            if (jakartaPersistenceJdbcUrl != null && !jakartaPersistenceJdbcUrl.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.url", jakartaPersistenceJdbcUrl);
            }
            if (jakartaPersistenceJdbcUser != null && !jakartaPersistenceJdbcUser.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.user", jakartaPersistenceJdbcUser);
            }
            if (jakartaPersistenceJdbcPassword != null && !jakartaPersistenceJdbcPassword.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.password", jakartaPersistenceJdbcPassword);
            }
            if (jakartaPersistenceSchemaGenerationDatabaseAction != null && !jakartaPersistenceSchemaGenerationDatabaseAction.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.database.action", jakartaPersistenceSchemaGenerationDatabaseAction);
            }
            if (jakartaPersistenceSchemaGenerationScriptsAction != null && !jakartaPersistenceSchemaGenerationScriptsAction.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.scripts.action", jakartaPersistenceSchemaGenerationScriptsAction);
            }
            if (jakartaPersistenceSchemaGenerationCreateSource != null && !jakartaPersistenceSchemaGenerationCreateSource.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.create-source", jakartaPersistenceSchemaGenerationCreateSource);
            }
            if (jakartaPersistenceSchemaGenerationDropSource != null && !jakartaPersistenceSchemaGenerationDropSource.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.drop-source", jakartaPersistenceSchemaGenerationDropSource);
            }
            if (jakartaPersistenceCreateDatabaseSchemas != null && !jakartaPersistenceCreateDatabaseSchemas.isEmpty()) {
                applicable.setProperty("jakarta.persistence.create-database-schemas", jakartaPersistenceCreateDatabaseSchemas);
            }
            if (hibernateDialect != null && !hibernateDialect.isEmpty()) {
                applicable.setProperty("hibernate.dialect", hibernateDialect);
            }
            if (hibernateHbm2ddlAuto != null && !hibernateHbm2ddlAuto.isEmpty()) {
                applicable.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
            }
            if (hibernateShowSql != null && !hibernateShowSql.isEmpty()) {
                applicable.setProperty("hibernate.show_sql", hibernateShowSql);
            }
            if (hibernateFormatSql != null && !hibernateFormatSql.isEmpty()) {
                applicable.setProperty("hibernate.format_sql", hibernateFormatSql);
            }
            if (hibernateConnectionPoolSize != null && !hibernateConnectionPoolSize.isEmpty()) {
                applicable.setProperty("hibernate.connection_pool_size", hibernateConnectionPoolSize);
            }
            if (hibernateConnectionDatasource != null && !hibernateConnectionDatasource.isEmpty()) {
                applicable.setProperty("hibernate.connection.datasource", hibernateConnectionDatasource);
            }
            if (hibernateCacheRegionFactoryClass != null && !hibernateCacheRegionFactoryClass.isEmpty()) {
                applicable.setProperty("hibernate.cache.region.factory_class", hibernateCacheRegionFactoryClass);
            }
            if (hibernateDefaultSchema != null && !hibernateDefaultSchema.isEmpty()) {
                applicable.setProperty("hibernate.default_schema", hibernateDefaultSchema);
            }
            // If you want to use a JTA DataSource, you can set it here
            if (jakartaJtaDataSource != null && !jakartaJtaDataSource.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jtaDataSource", jakartaJtaDataSource);
            }
            // If you want to use a non-JTA DataSource, you can set it here
            if (jakartaNonJtaDataSource != null && !jakartaNonJtaDataSource.isEmpty()) {
                applicable.setProperty("jakarta.persistence.nonJtaDataSource", jakartaNonJtaDataSource);
            }
 
            appEntityManagerFactoryProperties.compareAndSet(null, applicable);
        }
        return appEntityManagerFactoryProperties.get();
    }

    @Bean(name = "appEntityManagerFactory")
    @PersistenceContext(unitName = "AppDbPU")
    public EntityManagerFactory createAppEntityManagerFactory(
        @Qualifier("appDataSource") DataSource secDataSource, Environment env) {
        if (refAppEntityManagerFactory.get() == null) {
            // Create the EntityManagerFactory using the Jakarta Persistence API
            try {
                Properties emfProperties = getAppEntityManagerFactoryProperties();
                System.err.println("Creating appEntityManagerFactory with properties:");
                emfProperties.forEach((key, value) -> {
                    if (value instanceof String) {
                        String s = (String)value;
                        System.err.println("  " + key + " = " + s);
                    }
                    else {
                        String classname = value.getClass().getName();
                        System.err.println("  " + key + " = instanceof(" + classname + ")");
                    }
                });
                EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName, emfProperties);
                refAppEntityManagerFactory.compareAndSet(null, emf);
            } catch (Exception e) {
                System.err.println("ERROR: Persistence.createEntityManagerFactory(\"" + persistenceUnitName + "\", emfProperties) threw " + e.getClass().getName() + ": " + e.getMessage());
                e.printStackTrace(System.err);
                throw e;
            }
        }
        return refAppEntityManagerFactory.get();
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            emf = refAppEntityManagerFactory.get();
            if (emf == null) {
                throw new IllegalStateException("EntityManagerFactory is not initialized. Please ensure that AppDbConfig is properly configured.");
            }
        }
        return emf.createEntityManager();
    }

    public static void releaseEntityManager(EntityManager em) {
        if (em != null && em.isOpen()) {
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback(); // Rollback any active transaction
                }
            }
            catch (Exception e) {
                System.err.println("ERROR: Exception " + e.getClass().getCanonicalName() + " caught and ignored during transaction rollback of AppDb entity manager prior to closure: - " + e.getMessage());
                e.printStackTrace(System.err);
            }
            em.close(); // Close the EntityManager to release resources
        }
    }

    public static void flush() {
        if (emf != null) {
            emf.getCache().evictAll(); // Clear the cache to ensure the new entities are visible
        }
    }
}