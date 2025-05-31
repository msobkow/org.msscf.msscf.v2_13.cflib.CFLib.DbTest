package org.msscf.msscf.v2_13.cflib.CFLib.DbTest;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
    "org.msscf.msscf.v2_13.cflib.CFLib.dbutil",
    "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb",
    "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb"
})
@EnableJpaRepositories(
    basePackages = {
        "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb",
        "org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb"
    }
)
public class DataConfiguration {
    // No additional configuration needed here
}
