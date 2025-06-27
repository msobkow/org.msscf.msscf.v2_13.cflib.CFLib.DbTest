package org.msscf.msscf.v2_13.cflib.CFLib.DbTest;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.*;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

import jakarta.persistence.EntityManager;

public class CFDbTestRunner {
    public void run(EntityManager em) throws Exception {
        // Example: create and save a SecUser and SecSession using JPA
        em.getTransaction().begin();
        SecDbUser user = new SecDbUser(new CFLibDbKeyHash256("0123456789abcdef"));
        em.persist(user);
        SecDbManager manager = new SecDbManager(new CFLibDbKeyHash256("fedcba9876543210"), user);
        em.persist(manager);
        System.out.println("Sample SecDbUser and SecDbManagers created.");
        em.getTransaction().commit();
    }
}
