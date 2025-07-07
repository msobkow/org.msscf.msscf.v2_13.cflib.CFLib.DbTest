package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;

import java.time.LocalDateTime;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.ISecDbManagerService;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.ISecDbUserService;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbManager;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbUser;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service(value="TestSecDb")
public class TestSecDb implements ITestSecDb {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private EntityManagerFactory secEntityManagerFactory;

    @Autowired
    @Qualifier("secUserService")
    private ISecDbUserService secUserService;

    @Autowired
    @Qualifier("secManagerService")
    private ISecDbManagerService secManagerService;

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, dontRollbackOn = NoResultException.class)
    @PersistenceContext(unitName = "SecDbPU")
    public String performTests() {
        StringBuffer responseMessage = new StringBuffer();
        EntityManager em = null;
        try {
            em = secEntityManagerFactory.createEntityManager();
            LocalDateTime now = LocalDateTime.now();
            CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
            CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
            SecDbManager manager = SecDbManager.find(em, mgrpid);
            if (manager == null) {
                manager = new SecDbManager(mgrpid, "system", "admin", "1", "System Administration", "1",
                          null, null,
                          now, mgrpid,
                          now, mgrpid);
                manager = secManagerService.create(em, manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 created, update stamp is " + manager.getUpdatedAt().toString();
                responseMessage.append(msg);
            }
            else {
                manager.setUpdatedBy(adminpid);
                manager = secManagerService.update(em, manager);
                String msg = "INFO: Sample SecDbManager 'system' fedcba9876543210 updated, update stamp is " + manager.getUpdatedAt().toString();
                responseMessage.append(msg);
            }
            SecDbUser user = secUserService.find(em, adminpid);
            if (user == null) {
                user = new SecDbUser(adminpid, "admin", "root", "1", now, mgrpid, now, mgrpid);
                user = secUserService.create(em, user);
                String msg = "INFO: Sample SecDbUser 'admin' 0123456789abcdef created.";
                responseMessage.append(msg);
            }
            else {
                user.setUpdatedBy(adminpid);
                user = secUserService.update(em, user);
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
