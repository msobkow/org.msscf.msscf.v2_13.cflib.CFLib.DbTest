package org.msscf.msscf.v2_13.cflib.CFLib.DbTest;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbConfig;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb.AppDbConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:application.properties", "file:${user.home}/.cfdbtest.properties"})
public class ApplicationConfig {

    private final SecDbConfig secDbConfig;
    private final AppDbConfig appDbConfig;

    public ApplicationConfig(SecDbConfig secDbConfig, AppDbConfig appDbConfig) {
        this.secDbConfig = secDbConfig;
        this.appDbConfig = appDbConfig;
    }

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String hibernateDdlAuto;

    @Bean
    public SecDbConfig getSecDbConfig() {
        return secDbConfig;
    }

    @Bean
    public AppDbConfig getAppDbConfig() {
        return appDbConfig;
    }
}
