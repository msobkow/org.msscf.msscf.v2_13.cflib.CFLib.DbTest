package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import jakarta.persistence.*;
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service(value="AppDbAddressService")
@PersistenceContext(unitName = "AppDbPU")
public class AppDbAddressService implements IAppDbAddressService {

    @Autowired
    @Qualifier("appEntityManagerFactory")
    private EntityManagerFactory appEntityManagerFactory;

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public AppDbAddress find(EntityManager em, CFLibDbKeyHash256 pid) {
        boolean newEM = false;
        if (em == null) {
            em = appEntityManagerFactory.createEntityManager();
            newEM = true;
        }
        try {
            if (pid == null) {
                return null;
            }
            AppDbAddress addr = em.find(AppDbAddress.class, pid);
            return addr;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: AppDbAddressService.find() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for AppDbAddress instance with pid: " + pid + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public AppDbAddress findByRefUIDName(EntityManager em, CFLibDbKeyHash256 refUID, String name) {
        if (refUID == null || refUID.isNull()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            em = appEntityManagerFactory.createEntityManager();
            newEM = true;
        }
        try {
            AppDbAddress addr = (AppDbAddress)em.createQuery("select u from AppDbAddress u where u.refuid = :refUID and u.addrname = :addrName").setParameter("refUID", refUID).setParameter("addrName", name).getSingleResultOrNull();
            return addr;
        }
        catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            System.err.println("ERROR: AppDbAddressService.findByRefUIDName() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for AppDbAddress instance with RefUID: " + refUID.asString() + ", name: \"" + name + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public List<AppDbAddress> findByRefUID(EntityManager em, CFLibDbKeyHash256 refUID) {
        if (refUID == null || refUID.isNull()) {
            return new ArrayList<>();
        }
        boolean newEM = false;
        if (em == null) {
            em = appEntityManagerFactory.createEntityManager();
            newEM = true;
        }
        try {
            List<AppDbAddress> listOfAddr = (List<AppDbAddress>)em.createQuery("select u from AppDbAddress u where u.refUID = :refUID").setParameter("refUID", refUID).getResultList();
            if (listOfAddr == null) {
                listOfAddr = new ArrayList<>();
            }
            return listOfAddr;
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
        catch (Exception e) {
            System.err.println("ERROR: AppDbAddressService.findByRefUID() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for AppDbAddress instances with refUID: \"" + refUID.asString() + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public AppDbAddress create(EntityManager em, AppDbAddress data) {
        if (data == null) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            em = appEntityManagerFactory.createEntityManager();
            newEM = true;
        }
        try {
            if (data.getPid() == null || data.getPid().isNull()) {
                data.setPid(new CFLibDbKeyHash256(0));
            }
            LocalDateTime now = LocalDateTime.now();
            data.setCreatedAt(now);
            data.setUpdatedAt(now);
            // em.getTransaction().begin();
            AppDbAddress existing = em.find(AppDbAddress.class, data.getPid());
            if (existing != null) {
                return existing;
            }
            em.persist(data);
            // em.getTransaction().commit();
            return data;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = NoResultException.class)
    public AppDbAddress update(EntityManager em, AppDbAddress data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new IllegalArgumentException("Cannot update AppDbAddress with null primary identifier (pid)");
        }
        boolean newEM = false;
        if (em == null) {
            em = appEntityManagerFactory.createEntityManager();
            newEM = true;
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            data.setUpdatedAt(now);
            data = em.merge(data);
            return data;
        }
        catch (Exception e) {
            System.err.println("ERROR: AppDbAddressService.update() Caught and rethrew " + e.getClass().getCanonicalName() + " while update AppDbAddress with pid: " + data.getPid() + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }
}
