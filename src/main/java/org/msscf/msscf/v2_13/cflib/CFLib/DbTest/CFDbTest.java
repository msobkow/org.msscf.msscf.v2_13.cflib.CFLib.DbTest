// Description: Main for CFCli

/*
 *  MSS Code Factory MssCF 2.13 CFDbTest
 *
 *	Copyright 2021 Mark Stephen Sobkow
 *
 *	This file is part of MSS Code Factory.
 *
 *	MSS Code Factory is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	MSS Code Factory is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with MSS Code Factory.  If not, see https://www.gnu.org/licenses/.
 *
 *	Please contact Mark Stephen Sobkow at mark.sobkow@gmail.com for commercial licensing.
 */

package org.msscf.msscf.v2_13.cflib.CFLib.DbTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class
})
@EntityScan(basePackages = "org.msscf.msscf.v2_13.cflib.CFLib.CFDbTest.secdb")
public class CFDbTest
{
    public static void main(String[] args) {
        String userHome = System.getProperty("user.home");
        File userPropsFile = new File(userHome, ".cfdbtest.properties");

        // Load default properties from the compiled-in resource
        if (!userPropsFile.exists()) {
            try (var in = CFDbTest.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (in == null) {
                    throw new IOException("Default application.properties resource not found in classpath");
                }
                Files.copy(in, userPropsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("A new user properties file has been created at: " + userPropsFile.getAbsolutePath());
                System.out.println("Please customize this file before running the application again.");
                System.exit(0);
            } catch (IOException e) {
                System.err.println("Failed to create user properties file: " + e.getMessage());
                System.exit(1);
            }
        }

        Properties userProperties = new Properties();
        File userFile = new File(System.getProperty("user.home"), ".cfdbtest.properties");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                userProperties.load(fis);
                // System.getProperties().putAll(userProperties);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
            }
        }
        else {
            throw new RuntimeException("No user properties file found at " + userFile.getAbsolutePath());
        }
        Properties merged = new Properties();
        merged.putAll(System.getProperties());
        merged.putAll(userProperties);
        System.getProperties().putAll(merged);

        SpringApplication app = new SpringApplication(CFDbTest.class);
        app.addInitializers((applicationContext) -> {
            ConfigurableEnvironment env = applicationContext.getEnvironment();
            Properties userProps = new Properties();
            File userF = new File(System.getProperty("user.home"), ".cfdbtest.properties");
            if (userF.exists()) {
                try (FileInputStream fis = new FileInputStream(userF)) {
                    userProps.load(fis);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load user properties from .cfdbtest.properties", e);
                }
            }
            env.getPropertySources().addLast(new org.springframework.core.env.PropertiesPropertySource("userProperties", userProps));
        });
        app.run(args);
    }
}
