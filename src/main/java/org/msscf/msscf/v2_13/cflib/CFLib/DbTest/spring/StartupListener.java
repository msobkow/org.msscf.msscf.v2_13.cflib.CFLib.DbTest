package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.*;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;

@Component
public class StartupListener {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private EntityManagerFactory secDbEMF;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {

        try (EntityManager em = secDbEMF.createEntityManager()) {
            // Example: create and save a SecUser and SecSession using JPA
            em.getTransaction().begin();
            SecDbUser user = new SecDbUser(new CFLibDbKeyHash256("0123456789abcdef"));
            em.merge(user);
            SecDbManager manager = new SecDbManager(new CFLibDbKeyHash256("fedcba9876543210"), user);
            em.merge(manager);
            System.out.println("Sample SecDbUser and SecDbManagers created.");
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Your logic here
        System.out.println("DbTest has created a sample SecDbUser and a sample SecDbManager!");
    }
}