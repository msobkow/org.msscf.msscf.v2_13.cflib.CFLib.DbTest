/*
 *	MSS Code Factory CFLib 2.13 Database Testing
 *
 *	Copyright (C) 2016-2025 Mark Stephen Sobkow (mailto:mark.sobkow@gmail.com)
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
package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;
import java.time.LocalDateTime;
import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb.AppDbAddress;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb.AppDbAddressService;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbUserService;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service("TestAppDb")
public class TestAppDb {

    @Autowired
    @Qualifier("appEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean appEntityManagerFactoryBean;

    @Autowired
    private AppDbAddressService appDbAddressService;
    
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, dontRollbackOn = NoResultException.class)
    // @PersistenceContext(unitName = "AppDbPU")
    public String performTests(EntityManager em) {
        StringBuffer responseMessage = new StringBuffer();
        LocalDateTime now = LocalDateTime.now();
        // CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
        CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
        List<AppDbAddress> addresses = appDbAddressService.findByRefUID(mgrpid);
        if (addresses == null || addresses.isEmpty()) {
            AppDbAddress appAddress = new AppDbAddress(new CFLibDbKeyHash256(0), mgrpid, "Home", "Mark Sobkow", "19", "207 Seventh Avenue North", null, "Yorkton", "SK", "Canada", "S3N 0X3", now, mgrpid, now, mgrpid);
            appAddress = appDbAddressService.create(appAddress);
            responseMessage.append("Sample AppDbAddress for Manager " + mgrpid.toString() + " created in AppDb.");
        } else {
            responseMessage.append("Sample AppDbAddress already exists for Manager " + mgrpid.toString() + ", or at least there isn't an empty list we can assume indicates a clean database");
        }
        return responseMessage.toString();
    }
}
