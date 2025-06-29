package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import org.msscf.msscf.v2_13.cflib.CFLib.CFLib;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.*;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;

@Component
public class StartupListener {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private EntityManagerFactory secEntityManagerFactory;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {

        try (EntityManager em = SecDbConfig.getEntityManager()) {
            // Example: create and save a SecUser and SecSession using JPA
            em.getTransaction().begin();
            LocalDateTime now = LocalDateTime.now();
            CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
            SecDbManager manager = SecDbManager.find(em, mgrpid);
            if (manager == null) {
                manager = new SecDbManager(mgrpid, "system", "admin", "1", "System Administration", "1",
                          null, null,
                          now, mgrpid,
                          now, mgrpid);
                manager = SecDbManager.create(em, manager);
            }
            CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
            SecDbUser user = SecDbUser.find(em, adminpid);
            if (user == null) {
                user = new SecDbUser(adminpid, "admin", "root", "1", now, mgrpid, now, mgrpid);
                user = SecDbUser.create(em, user);
            }
            System.out.println("Sample SecDbUser and SecDbManagers created.");
            em.getTransaction().commit();
            SecDbConfig.flush(); // Ensure the changes are flushed to the database
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Your logic here
        System.out.println("DbTest has created a sample SecDbUser and a sample SecDbManager!");
    }
}