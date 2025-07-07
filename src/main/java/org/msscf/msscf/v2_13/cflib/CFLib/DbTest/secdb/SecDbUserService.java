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

@Service(value="SecDbUserService")
@PersistenceContext(unitName = "SecDbPU")
public class SecDbUserService implements ISecDbUserService {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactory;
    
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser find(EntityManager em, CFLibDbKeyHash256 pid) {
        if (pid == null) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbUserService.find(em,pid) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            SecDbUser user = em.find(SecDbUser.class, pid);
            return user;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbUserService.find() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbUser instance with pid: " + pid + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser findByName(EntityManager em, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbUserService.findByName(em,name) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            SecDbUser user = (SecDbUser)em.createQuery("select u from SecDbUser u where u.username = :name").setParameter("name", name).getSingleResultOrNull();
            return user;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbUserService.findByName() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbUser instance with name: \"" + name + "\" - " + e.getMessage());
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
                String msg = "ERROR: SecDbUserService.findByEmail(em,email) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            List<SecDbUser> listOfUser = (List<SecDbUser>)em.createQuery("select u from SecDbUser u where u.email = :email").setParameter("email", email).getResultList();
            if (listOfUser == null) {
                listOfUser = new ArrayList<>();
            }
            return listOfUser;
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbUserService.findByEmail() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbUser instances with email: \"" + email + "\" - " + e.getMessage());
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
                String msg = "ERROR: SecDbUserService.findByMemberDeptCode(em,memberDeptCode) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            List<SecDbUser> listOfUser = (List<SecDbUser>)em.createQuery("select u from SecDbUser u where u.member_deptcode = :deptcode").setParameter("deptcode", memberDeptCode).getResultList();
            if (listOfUser == null) {
                listOfUser = new ArrayList<>();
            }
            return listOfUser;
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbUserService.findByMemberDeptCode() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbUser instances with member_deptcode: \"" + memberDeptCode + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser create(EntityManager em, SecDbUser data) {
        if (data == null) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbUserService.create(em,data) secEntityManagerFactory.getObject() returns null";
                System.err.println(msg);
                throw new IllegalStateException(msg);
            }
            else {
                em = f.createEntityManager();
            }
            newEM = true;
        }
        try {
            if (data.getPid() == null) {
                data.setPid(new CFLibDbKeyHash256(0));
            }
            LocalDateTime now = LocalDateTime.now();
            data.setCreatedAt(now);
            data.setUpdatedAt(now);
            try {
                SecDbUser existing = em.find(SecDbUser.class, data.getPid());
                if (existing != null) {
                    return existing;
                }
            }
            catch( NoResultException ex) {
            }
            em.persist(data);
            return data;
        } catch (Exception e) {
            System.err.println("ERROR: SecDbUserService.create(em,data) Caught and rethrew " + e.getClass().getCanonicalName() + " while creating SecDbUser instance with pid: " + data.getPid().asString() + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser update(EntityManager em, SecDbUser data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new IllegalArgumentException("Cannot update SecDbUser with null primary identifier (pid)");
        }
        boolean newEM = false;
        if (em == null) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f == null) {
                String msg = "ERROR: SecDbUserService.update(em,data) secEntityManagerFactory.getObject() returns null";
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
            System.err.println("ERROR: SecDbUserService.update() Caught and rethrew " + e.getClass().getCanonicalName() + " while update SecDbUser with pid: " + data.getPid() + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }
}