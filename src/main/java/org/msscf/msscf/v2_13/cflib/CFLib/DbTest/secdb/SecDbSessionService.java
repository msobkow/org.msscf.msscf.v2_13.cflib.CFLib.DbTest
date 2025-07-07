package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Service(value="SecDbSessionService")
@PersistenceContext(unitName = "SecDbPU")
public class SecDbSessionService {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactory;

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbSession find(EntityManager em, CFLibDbKeyHash256 pid) {
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbSessionService.find(em,pid) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            if (pid == null) {
                return null;
            }
            SecDbSession manager = em.find(SecDbSession.class, pid);
            return manager;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbSessionService.find() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbSession instance with pid: " + pid + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbSession> findByUserPID(EntityManager em, CFLibDbKeyHash256 secUserPID) {
        if (secUserPID == null || secUserPID.isNull()) {
            return new ArrayList<>();
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbSessionService.findByName(em,name) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            List<SecDbSession> list = (List<SecDbSession>)em.createQuery("select s from SecDbSession s where s.user_pid = :secUserPID order by created_at desc").setParameter("secUserPID", secUserPID).getResultList();
            return list;
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbSessionService.findByUserPID() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbSession instance with user_pid: \"" + secUserPID.asString() + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbSession create(EntityManager em, SecDbSession data) {
        boolean newEM = false;
        boolean pidAssigned = false;
        if (data == null) {
            return null;
        }

        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbSessionService.create(em,data) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {

            LocalDateTime now = LocalDateTime.now();
            data.setCreatedAt(now);

            if (data.getPid() == null || data.getPid().isNull()) {
                data.setPid(new CFLibDbKeyHash256(0));
                pidAssigned = true;
            }

            SecDbSession existing;
            try {
                existing = em.find(SecDbSession.class, data.getPid());
            } catch (NoResultException e) {
                existing = null;
            }
            if (existing != null) {
                return existing;
            }

            em.persist(data);

            return data;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbSessionService.create() Caught and rethrew " + e.getClass().getCanonicalName() + " while creating SecDbSession with newly assigned pid: " + data.getPid() + " - " + e.getMessage());
            if (pidAssigned) {
                System.err.println("RECOV: Resetting newly assigned pid to null");
                data.setPid(null);
            }
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbSession update(EntityManager em, SecDbSession data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new IllegalArgumentException("Cannot update SecDbSession with null primary identifier (pid)");
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbSessionService.update(em,data) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            data = em.merge(data);
            return data;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbSessionService.update() Caught and rethrew " + e.getClass().getCanonicalName() + " while updating SecDbSession with pid: " + data.getPid() + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

}
