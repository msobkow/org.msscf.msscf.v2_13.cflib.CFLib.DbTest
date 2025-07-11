package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;

import java.time.LocalDateTime;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbManager;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbManagerService;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    // @PersistenceContext(unitName = "SecDbPU")
    public String performTests(EntityManager em) {
        StringBuffer responseMessage = new StringBuffer();
        try {
            if (em == null) {
                EntityManagerFactory f = secEntityManagerFactory.getObject();
                if (f == null) {
                    String msg = "ERROR: TestSeccDb.performTests() secEntityManagerFactory.getObject() returns null";
                    System.err.println(msg);
                    throw new IllegalStateException(msg);
                }
                else {
                    em = f.createEntityManager();
                }
            }
            // if (secUserService == null) {
            //     secUserService = applicationContext.getBean(SecDbUserService.class);
            // }
            // if (secManagerService == null) {
            //     secManagerService = applicationContext.getBean(SecDbManagerService.class);
            // }
            LocalDateTime now = LocalDateTime.now();
            CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
            CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
            SecDbManager manager = secDbManagerService.find(em, mgrpid);
            if (manager == null) {
                manager = new SecDbManager(mgrpid, "system", "admin", "1", "System Administration", "1",
                          null, null,
                          now, mgrpid,
                          now, mgrpid);
                manager = secDbManagerService.create(em, manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 created, update stamp is " + manager.getUpdatedAt().toString();
                responseMessage.append(msg);
            }
            else {
                manager.setUpdatedBy(adminpid);
                manager = secDbManagerService.update(em, manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 updated, update stamp is " + manager.getUpdatedAt().toString();
                responseMessage.append(msg);
            }
            SecDbUser user = secDbUserService.find(em, adminpid);
            if (user == null) {
                user = new SecDbUser(adminpid, "admin", "root", "1", now, mgrpid, now, mgrpid);
                user = secDbUserService.create(em, user);
                String msg = "INFO: Sample SecDbUser 'admin' 0123456789abcdef created.";
                responseMessage.append(msg);
            }
            else {
                user.setUpdatedBy(adminpid);
                user = secDbUserService.update(em, user);
                String msg = "INFO: Sample SecDbUser 'admin' 0123456789abcdef updated, update stamp is " + user.getUpdatedAt().toString();
                responseMessage.append(msg);
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
