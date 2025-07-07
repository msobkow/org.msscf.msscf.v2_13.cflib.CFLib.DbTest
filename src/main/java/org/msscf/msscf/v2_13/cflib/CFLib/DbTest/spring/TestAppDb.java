package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;

import java.time.LocalDateTime;
import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb.AppDbAddress;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb.IAppDbAddressService;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service(value="TestAppDb")
public class TestAppDb implements ITestAppDb {

    @Autowired
    @Qualifier("appEntityManagerFactory")
    private static EntityManagerFactory appEntityManagerFactory;

    @Autowired
    @Qualifier("AppDbAddressService")
    private static IAppDbAddressService appDbAddressService;
    
    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, dontRollbackOn = NoResultException.class)
    @PersistenceContext(unitName = "AppDbPU")
    public String performTests() {
        StringBuffer responseMessage = new StringBuffer();
        LocalDateTime now = LocalDateTime.now();
        // CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
        CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
        EntityManager em = null;
        try {
            em = appEntityManagerFactory.createEntityManager();
            List<AppDbAddress> addresses = appDbAddressService.findByRefUID(em, mgrpid);
            if (addresses == null || addresses.isEmpty()) {
                AppDbAddress appAddress = new AppDbAddress(new CFLibDbKeyHash256(0), mgrpid, "Home", "Mark Sobkow", "19", "207 Seventh Avenue North", null, "Yorkton", "SK", "Canada", "S3N 0X3", now, mgrpid, now, mgrpid);
                appAddress = appDbAddressService.create(em, appAddress);
                System.err.println("Sample AppDbAddress for Manager " + mgrpid.asString() + " created in AppDb.");
            } else {
                System.err.println("Sample AppDbAddress already exists for Manager " + mgrpid.asString() + ", or at least there isn't an empty list we can assume indicates a clean database");
            }
        }
        catch (Exception e) {
            String msg = "ERROR: TestAppDb.performTests() Caught and rethrew " + e.getClass().getCanonicalName() + " while querying or inserting the sample AppDbAddress for the Manager " + mgrpid.asString() + " - " + e.getMessage();
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
