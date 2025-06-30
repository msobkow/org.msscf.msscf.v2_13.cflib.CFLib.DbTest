package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.CFLib;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb.AppDbAddress;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb.AppDbConfig;
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

        try (EntityManager secEM = SecDbConfig.getEntityManager()) {
            // Example: create and save a SecUser and SecSession using JPA
            // secEM.getTransaction().begin();
            LocalDateTime now = LocalDateTime.now();
            CFLibDbKeyHash256 mgrpid = new CFLibDbKeyHash256("fedcba9876543210");
            SecDbManager manager = SecDbManager.find(secEM, mgrpid);
            if (manager == null) {
                manager = new SecDbManager(mgrpid, "system", "admin", "1", "System Administration", "1",
                          null, null,
                          now, mgrpid,
                          now, mgrpid);
                manager = SecDbManager.create(secEM, manager);
            }
            CFLibDbKeyHash256 adminpid = new CFLibDbKeyHash256("0123456789abcdef");
            SecDbUser user = SecDbUser.find(secEM, adminpid);
            if (user == null) {
                user = new SecDbUser(adminpid, "admin", "root", "1", now, mgrpid, now, mgrpid);
                user = SecDbUser.create(secEM, user);
            }
            System.out.println("Sample SecDbUser and SecDbManagers created.");
            // secEM.getTransaction().commit();
            SecDbConfig.flush(); // Ensure the changes are flushed to the database

            try (EntityManager appEM = AppDbConfig.getEntityManager()) {
                List<AppDbAddress> addresses = AppDbAddress.findByRefUID(appEM, mgrpid);
                if (addresses == null || addresses.isEmpty()) {
                    AppDbAddress appAddress = new AppDbAddress(new CFLibDbKeyHash256(0), mgrpid, "Home", "Mark Sobkow", "19", "207 Seventh Avenue North", null, "Yorkton", "SK", "Canada", "S3N 0X3", now, mgrpid, now, mgrpid);
                    appAddress = AppDbAddress.create(appEM, appAddress);
                    System.out.println("Sample AppDbAddress created in AppDb.");
                } else {
                    System.out.println("AppDbAddress already exists for the manager PID.");
                }
                AppDbConfig.releaseEntityManager(appEM);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SecDbConfig.releaseEntityManager(secEM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Your logic here
        System.out.println("DbTest has created a sample SecDbUser, a sample SecDbManager, and a sample AppDbAddress!");
    }
}