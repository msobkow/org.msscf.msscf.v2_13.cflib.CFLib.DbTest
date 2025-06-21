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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class CFDbTest
{
    public static void main(String[] args) {
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
