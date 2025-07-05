package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Entity
@DiscriminatorValue("1")
@Table(
    name = "sec_mgr", schema = "secdb",
    indexes = {
        @Index(name = "sec_mgr_deptcode_ax", columnList = "deptcode", unique = true)
    }
)
@Transactional(Transactional.TxType.SUPPORTS)
public class SecDbManager extends SecDbUser {
    
    public static final int TITLE_SIZE = 64;
    public static final int DEPARTMENT_CODE_SIZE = 32;

    @Column(name = "title", length = TITLE_SIZE, nullable = false)
    private String title = "";

    @Column(name = "deptcode", length = DEPARTMENT_CODE_SIZE, nullable = false, unique = true)
    private String departmentCode = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdeptof")
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "subdeptof", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private SecDbManager subDepartmentOf;

    @OneToMany(mappedBy = "subDepartmentOf", fetch = FetchType.LAZY)
    private Set<SecDbManager> departments = new HashSet<>();

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private static EntityManagerFactory secEntityManagerFactory;

    public SecDbManager() {
        super();
    }

    public SecDbManager(CFLibDbKeyHash256 pid) {
        super(pid);
        this.title = "";
        this.departmentCode = "";
    }

    public SecDbManager(CFLibDbKeyHash256 pid, SecDbUser user) {
        super(pid, user.getUsername(), user.getEmail());
        this.title = "";
        this.departmentCode = "";
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email) {
        super(pid, username, email);
        this.title = "";
        this.departmentCode = "";
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String title, String departmentCode) {
        super(pid, username, email);
        this.title = title;
        this.departmentCode = departmentCode;
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String title, String departmentCode, SecDbManager subDepartmentOf) {
        super(pid, username, email);
        this.title = title;
        this.departmentCode = departmentCode;
        this.subDepartmentOf = subDepartmentOf;
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String title, String departmentCode, SecDbManager subDepartmentOf, Set<SecDbManager> departments) {
        super(pid, username, email);
        this.title = title;
        this.departmentCode = departmentCode;
        this.subDepartmentOf = subDepartmentOf;
        this.departments = departments != null ? departments : new HashSet<>();
    }

    public SecDbManager(CFLibDbKeyHash256 pid, String username, String email, String memberDeptCode, String title, String departmentCode,
                          SecDbManager subDepartmentOf, Set<SecDbManager> departments,
                          java.time.LocalDateTime createdAt, CFLibDbKeyHash256 createdBy,
                          java.time.LocalDateTime updatedAt, CFLibDbKeyHash256 updatedBy) {
        super(pid, username, email, memberDeptCode, createdAt, createdBy, updatedAt, updatedBy);
        this.title = title;
        this.departmentCode = departmentCode;
        this.subDepartmentOf = subDepartmentOf;
        this.departments = departments != null ? departments : new HashSet<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public SecDbManager getSubDepartmentOf() {
        return subDepartmentOf;
    }

    public void setSubDepartmentOf(SecDbManager subDepartmentOf) {
        this.subDepartmentOf = subDepartmentOf;
    }

    public Set<SecDbManager> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<SecDbManager> departments) {
        this.departments = departments;
    }

    public void addDepartment(SecDbManager department) {
        if (department != null) {
            departments.add(department);
            department.setSubDepartmentOf(this);
        }
    }

    public void removeDepartment(SecDbManager department) {
        if (department != null) {
            departments.remove(department);
            department.setSubDepartmentOf(null);
        }
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof SecDbManager)) return -1;
        SecDbManager other = (SecDbManager) o;
        int cmp = super.compareTo(other);
        if (cmp != 0) return cmp;
        cmp = (this.title == null && other.title == null) ? 0 : ((this.title != null && this.title.equals(other.title)) ? 0 : (this.title == null ? -1 : (other.title == null ? 1 : this.title.compareTo(other.title))));
        if (cmp != 0) return cmp;
        cmp = (this.departmentCode == null && other.departmentCode == null) ? 0 : ((this.departmentCode != null && this.departmentCode.equals(other.departmentCode)) ? 0 : (this.departmentCode == null ? -1 : (other.departmentCode == null ? 1 : this.departmentCode.compareTo(other.departmentCode))));
        if (cmp != 0) return cmp;
        cmp = (this.subDepartmentOf == null && other.subDepartmentOf == null) ? 0 : ((this.subDepartmentOf != null && other.subDepartmentOf != null && this.subDepartmentOf.getPid().equals((other.subDepartmentOf.getPid()))) ? 0 : (this.subDepartmentOf == null ? -1 : (other.subDepartmentOf == null ? 1 : this.subDepartmentOf.getPid().compareTo(other.subDepartmentOf.getPid()))));
        return cmp;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public static SecDbManager find(EntityManager em, CFLibDbKeyHash256 pid) {
        boolean newEM = false;
        if (em == null) {
            em = secEntityManagerFactory.createEntityManager();
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
            System.err.println("ERROR: SecDbManager.find() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instance with pid: " + pid + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public static SecDbManager findByName(EntityManager em, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            em = secEntityManagerFactory.createEntityManager();
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
            System.err.println("ERROR: SecDbManager.findByName() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instance with name: \"" + name + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public static List<SecDbUser> findByEmail(EntityManager em, String email) {
        if (email == null || email.isEmpty()) {
            return new ArrayList<>();
        }
        boolean newEM = false;
        if (em == null) {
            em = secEntityManagerFactory.createEntityManager();
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
            System.err.println("ERROR: SecDbManager.findByEmail() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instances with email: \"" + email + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public static List<SecDbUser> findByMemberDeptCode(EntityManager em, String memberDeptCode) {
        if (memberDeptCode == null || memberDeptCode.isEmpty()) {
            return new ArrayList<>();
        }
        boolean newEM = false;
        if (em == null) {
            em = secEntityManagerFactory.createEntityManager();
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
            System.err.println("ERROR: SecDbManager.findByMemberDeptCode() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instances with member_deptcode: \"" + memberDeptCode + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public static SecDbManager findByDeptCode(EntityManager em, String deptCode) {
        if (deptCode == null || deptCode.isEmpty()) {
            return null;
        }
        boolean newEM = false;
        if (em == null) {
            em = secEntityManagerFactory.createEntityManager();
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
            System.err.println("ERROR: SecDbManager.findByDeptCode() Caught and rethrew " + e.getClass().getCanonicalName() + " while searching for SecDbManager instance with deptcode: \"" + deptCode + "\" - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }
    
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = NoResultException.class)
    public static SecDbManager create(EntityManager em, SecDbManager data) {
        boolean newEM = false;
        boolean pidAssigned = false;
        if (em == null) {
            em = secEntityManagerFactory.createEntityManager();
            newEM = true;
        }
        try {
            if (data == null) {
                return null;
            }

            if (data.getPid() == null || data.getPid().isNull()) {
                data.setPid(new CFLibDbKeyHash256(0));
                pidAssigned = true;
            }

            LocalDateTime now = LocalDateTime.now();
            data.setCreatedAt(now);
            data.setUpdatedAt(now);

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
            System.err.println("ERROR: SecDbManager.create() Caught and rethrew " + e.getClass().getCanonicalName() + " while creating SecDbManager with newly assigned pid: " + data.getPid() + " - " + e.getMessage());
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

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = NoResultException.class)
    public static SecDbManager update(EntityManager em, SecDbManager data) {
        boolean newEM = false;
        if (em == null) {
            em = secEntityManagerFactory.createEntityManager();
            newEM = true;
        }
        try {
            if (data == null) {
                return null;
            }
            if (data.getPid() == null || data.getPid().isNull()) {
                throw new IllegalArgumentException("Cannot update SecDbManager with null primary identifier (pid)");
            }
            LocalDateTime now = LocalDateTime.now();
            data.setUpdatedAt(now);
            data = em.merge(data);
            return data;
        }
        catch (Exception e) {
            System.err.println("ERROR: SecDbManager.update() Caught and rethrew " + e.getClass().getCanonicalName() + " while updating SecDbManager with pid: " + data.getPid() + " - " + e.getMessage());
            throw e;
        } finally {
            if (newEM) {
                em.close();
            }
        }
    }
}
