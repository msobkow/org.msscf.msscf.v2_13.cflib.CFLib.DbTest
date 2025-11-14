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

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbManager;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbManagerService;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbSession;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbSessionService;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbUser;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbUserService;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Service("TestSecDb")
public class TestSecDb {
    
    @Autowired
    @Qualifier("secEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactory;

    @Autowired
    private SecDbUserService secDbUserService;

    @Autowired
    private SecDbManagerService secDbManagerService;

    @Autowired
    private SecDbSessionService secDbSessionService;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    // @PersistenceContext(unitName = "SecDbPU")
    public String performTests(EntityManager em) {
        StringBuffer responseMessage = new StringBuffer();
        try {
            LocalDateTime now = LocalDateTime.now();
            CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
            CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
            SecDbManager manager = (SecDbManager)secDbManagerService.find(mgrpid);
            if (manager == null) {
                manager = new SecDbManager(mgrpid, "system", "admin", "1", "System Administration", "1",
                          null, null,
                          now, mgrpid,
                          now, mgrpid);
                manager = secDbManagerService.create(manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 created, update stamp is " + manager.getUpdatedAt().toString();
                responseMessage.append(msg);
            }
            else {
                manager.setUpdatedBy(adminpid);
                manager = secDbManagerService.update(manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 updated, update stamp is " + manager.getUpdatedAt().toString();
                responseMessage.append(msg);
            }
            SecDbUser user = secDbUserService.find(adminpid);
            if (user == null) {
                user = new SecDbUser(adminpid, "admin", "root", "1", now, mgrpid, now, mgrpid);
                user = secDbUserService.create(user);
                String msg = "INFO: Sample SecDbUser 'admin' 0123456789abcdef created.";
                responseMessage.append(msg);
            }
            else {
                user.setUpdatedBy(adminpid);
                user = secDbUserService.update(user);
                String msg = "INFO: Sample SecDbUser 'admin' 0123456789abcdef updated, update stamp is " + user.getUpdatedAt().toString();
                responseMessage.append(msg);
            }
            List<SecDbSession> managerSessions = secDbSessionService.findByUser(manager);
            if (managerSessions == null || managerSessions.isEmpty()) {
                SecDbSession sess = new SecDbSession(mgrpid, manager, "System initialization", now, null, null);
                sess = secDbSessionService.create(sess);
                String msg = "INFO Priming SecDbSession " + sess.getPid().toString() + " for system initialization created";
                responseMessage.append(msg);
            }
            else {
                if (managerSessions.size() == 1) {
                    SecDbSession sess = managerSessions.get(0);
                    if (sess.getTerminatedAt() == null) {
                        sess.setTerminatedAt(now);
                        sess.setSessTerminationInfo("First rerun auto-terminates the initialization session");
                        sess = secDbSessionService.update(sess);
                        String msg = "INFO Terminated last run SecDbSession " + sess.getPid().toString() + " from system initialization";
                        responseMessage.append(msg);
                    }
                    else {
                        String msg = "INFO SecDbSession " + sess.getPid().toString() + " from system initialization was terminated at " + sess.getTerminatedAt();
                        responseMessage.append(msg);
                    }
                }
                else {
                    String msg = "INFO Multiple SecDbSession instances indicate initialization happened some time ago.";
                    responseMessage.append(msg);
                }
            }
        }
        catch (Exception e) {
            String msg = "ERROR: TestSecDb.performTests() Caught and rethrew " + e.getClass().getCanonicalName() + " while modifying or creating the 'system' manager and the 'admin' user - " + e.getMessage();
            responseMessage.append(msg);
            System.err.println(msg);
            e.printStackTrace(System.err);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return responseMessage.toString();
    }
}
