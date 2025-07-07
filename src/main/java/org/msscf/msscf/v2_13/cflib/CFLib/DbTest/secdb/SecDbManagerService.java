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

@Service(value="SecDbManagerService")
@PersistenceContext(unitName = "SecDbPU")
public class SecDbManagerService {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactory;

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbManager find(EntityManager em, CFLibDbKeyHash256 pid) {
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbManagerService.find(em,pid) secEntityManagerFactory.getObject() returns null";
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
            SecDbManager manager = em.find(SecDbManager.class, pid);
            return manager;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbManagerService.find() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instance with pid: " + pid + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbManager findByName(EntityManager em, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbManagerService.findByName(em,name) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            SecDbManager manager = (SecDbManager)em.createQuery("select u from SecDbManager u where u.username = :name").setParameter("name", name).getSingleResultOrNull();
            return manager;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbManagerService.findByName() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instance with name: \"" + name + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbUser> findByEmail(EntityManager em, String email) {
        if (email == null || email.isEmpty()) {
            return new ArrayList<>();
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbManagerService.findByEmail(em,email) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            List<SecDbUser> listOfManager = (List<SecDbUser>)em.createQuery("select u from SecDbManager u where u.email = :email").setParameter("email", email).getResultList();
            if (listOfManager == null) {
                listOfManager = new ArrayList<>();
            }
            return listOfManager;
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbManagerService.findByEmail() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instances with email: \"" + email + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbUser> findByMemberDeptCode(EntityManager em, String memberDeptCode) {
        if (memberDeptCode == null || memberDeptCode.isEmpty()) {
            return new ArrayList<>();
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbManagerService.findByMemberDeptCode(em,pid) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            List<SecDbUser> listOfManager = (List<SecDbUser>)em.createQuery("select u from SecDbManager u where u.member_deptcode = :deptcode").setParameter("deptcode", memberDeptCode).getResultList();
            if (listOfManager == null) {
                listOfManager = new ArrayList<>();
            }
            return listOfManager;
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbManagerService.findByMemberDeptCode() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instances with member_deptcode: \"" + memberDeptCode + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbManager findByDeptCode(EntityManager em, String deptCode) {
        if (deptCode == null || deptCode.isEmpty()) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbManagerService.findByDeptCode(em,pid) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            SecDbManager manager = (SecDbManager)em.createQuery("select u from SecDbManager u where u.deptcode = :deptcode").setParameter("deptcode", deptCode).getSingleResultOrNull();
            return manager;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbManagerService.findByDeptCode() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instance with deptcode: \"" + deptCode + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbManager create(EntityManager em, SecDbManager data) {
        boolean newEM = false;
        boolean pidAssigned = false;
        if (data == null) {
            return null;
        }

        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbManagerService.create(em,data) secEntityManagerFactory.getObject() returns null";
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
            data.setUpdatedAt(now);

            if (data.getPid() == null || data.getPid().isNull()) {
                data.setPid(new CFLibDbKeyHash256(0));
                pidAssigned = true;
            }

            SecDbManager existing;
            try {
                existing = em.find(SecDbManager.class, data.getPid());
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
            System.err.println("ERROR: SecDbManagerService.create() Caught and rethrew " + e.getClass().getCanonicalName() + " while creating SecDbManager with newly assigned pid: " + data.getPid() + " - " + e.getMessage());
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
    public SecDbManager update(EntityManager em, SecDbManager data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new IllegalArgumentException("Cannot update SecDbManager with null primary identifier (pid)");
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbManagerService.update(em,data) secEntityManagerFactory.getObject() returns null";
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
            data.setUpdatedAt(now);
            data = em.merge(data);
            return data;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbManagerService.update() Caught and rethrew " + e.getClass().getCanonicalName() + " while updating SecDbManager with pid: " + data.getPid() + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }
}
