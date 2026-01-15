/*
 *	MSS Code Factory CFLib 2.13 Database Testing
 *
 *	Copyright (C) 2016-2026 Mark Stephen Sobkow (mailto:mark.sobkow@gmail.com)
 *	
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see &lt;https://www.gnu.org/licenses/&gt;.
 *	
 *	If you wish to modify and use this code without publishing your changes,
 *	or integrate it with proprietary code, please contact Mark Stephen Sobkow
 *	for a commercial license at mark.sobkow@gmail.com
 */
package org.msscf.msscf.v2_13.cflib.CFLib.DbTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@ComponentScan(basePackages = {
    "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb",   // for secdb services
    "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb",   // for appdb services
    "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring"   // if you have service beans here
})
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class
})
public class DbTest
{
    private static final AtomicReference<Properties> systemProperties = new AtomicReference<>(null);
    private static final AtomicReference<Properties> applicationProperties = new AtomicReference<>(null);
    private static final AtomicReference<Properties> userDefaultProperties = new AtomicReference<>(null);
    private static final AtomicReference<Properties> userProperties = new AtomicReference<>(null);
    private static final AtomicReference<Properties> mergedProperties = new AtomicReference<>(null);

    /**
     * Loads the application properties file from the application resources.
     */
    public static Properties getApplicationProperties() {
        if (applicationProperties.get() == null) {
            Properties props = new Properties();
            try (var in = DbTest.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (in != null) {
                    props.load(in);
                } else {
                    throw new RuntimeException("application.properties not found in classpath resources");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load application properties from application.properties", e);
            }
            applicationProperties.compareAndSet(null, props);
        }
        return applicationProperties.get();
    }

    /**
     * Loads the system properties, which hopefully haven't had the merge applied yet.
     */
    public static Properties getSystemProperties() {
        if (systemProperties.get() == null) {
            Properties props = new Properties();
            props.putAll(System.getProperties());
            systemProperties.compareAndSet(null, props);
        }
        return systemProperties.get();
    }
  
    /**
     * Loads the user default properties file from the application resources.
     */
    public static Properties getUserDefaultProperties() {
        if (userDefaultProperties.get() == null) {
            Properties props = new Properties();
            try (var in = DbTest.class.getClassLoader().getResourceAsStream("user-default.properties")) {
                if (in != null) {
                    props.load(in);
                } else {
                    throw new RuntimeException("user-default.properties not found in classpath resources");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user default properties from user-default.properties", e);
            }
            userDefaultProperties.compareAndSet(null, props);
        }
        return userDefaultProperties.get();
    }

    /**
     * Loads the user properties file from their home directory.
     */
    public static Properties getUserProperties() {
        if (userProperties.get() == null) {
            Properties props = new Properties();
            File userFile = new File(System.getProperty("user.home"), ".dbtest.properties");
            if (userFile.exists()) {
                try (FileInputStream fis = new FileInputStream(userFile)) {
                    props.load(fis);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load user properties from .dbtest.properties", e);
                }
            } else {
                try (var in = DbTest.class.getClassLoader().getResourceAsStream("user-default.properties")) {
                    if (in != null) {
                        Files.copy(in, userFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("A new user properties file has been created at: " + userFile.getAbsolutePath());
                        System.out.println("Please customize this file before running the application again.");
                        System.exit(0);
                    }
                    else {
                        var subin = DbTest.class.getClassLoader().getResourceAsStream("application.properties");
                        if (subin != null) {
                            Files.copy(subin, userFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("A new user properties file has been created at: " + userFile.getAbsolutePath());
                            System.out.println("Please customize this file before running the application again.");
                            System.exit(0);
                        } else {
                            throw new RuntimeException("user-default.properties and application.properties not found in classpath resources");
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to create user properties file \"" + userFile.getAbsolutePath() + "\": " + e.getMessage());
                    System.exit(1);
                }
            }
            userProperties.compareAndSet(null, props);
        }
        return userProperties.get();
    }

    /**
     * Merges the System and User properties, giving preference to the User properties.
     */
    public static Properties getMergedProperties() {
        if (mergedProperties.get() == null) {
            Properties merged = new Properties();
            merged.putAll(getApplicationProperties());
            merged.putAll(getUserDefaultProperties());
            merged.putAll(getSystemProperties());
            merged.putAll(getUserProperties());
            mergedProperties.compareAndSet(null, merged);
        }
        return mergedProperties.get();
    }

    public static void main(String[] args) {
        Properties applicationProperties = getApplicationProperties();
        Properties userDefaultProperties = getUserDefaultProperties();
        Properties systemProperties = getSystemProperties();
        Properties userProperties = getUserProperties();
        Properties mergedProperties = getMergedProperties();
        System.getProperties().putAll(mergedProperties);

        SpringApplication app = new SpringApplication(DbTest.class);
        app.addInitializers((applicationContext) -> {
            ConfigurableEnvironment env = applicationContext.getEnvironment();
            env.getPropertySources().addLast(new org.springframework.core.env.PropertiesPropertySource("userProperties", userProperties));
        });
        app.run(args);
    }
}
