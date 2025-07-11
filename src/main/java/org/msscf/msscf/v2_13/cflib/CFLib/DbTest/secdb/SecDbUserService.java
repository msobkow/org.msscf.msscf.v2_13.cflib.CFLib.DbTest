package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

@Service("SecDbUserService")
public class SecDbUserService {

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactory;
    
    @Autowired
    private SecDbUserRepository secDbUserRepository;

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser find(CFLibDbKeyHash256 pid) {
        return secDbUserRepository.findById(pid).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser findByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        SecDbUser probe = new SecDbUser();
        probe.setUsername(name);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<SecDbUser> example = Example.of(probe, matcher);

        return secDbUserRepository.findOne(example).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbUser> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return new ArrayList<>();
        }
        return secDbUserRepository.findByEmail(email);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbUser> findByMemberDeptCode(String memberDeptCode) {
        if (memberDeptCode == null || memberDeptCode.isEmpty()) {
            return new ArrayList<>();
        }
        return secDbUserRepository.findByMemberDeptCode(memberDeptCode);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser create(SecDbUser data) {
        if (data == null) {
            return null;
        }
        CFLibDbKeyHash256 originalPid = data.getPid();
        boolean generatedPid = false;
        try {
            if (data.getPid() == null) {
                data.setPid(new CFLibDbKeyHash256(0));
                generatedPid = true;
            }
            LocalDateTime now = LocalDateTime.now();
            data.setCreatedAt(now);
            data.setUpdatedAt(now);

            // Check if already exists
            if (data.getPid() != null && secDbUserRepository.existsById(data.getPid())) {
                return secDbUserRepository.findById(data.getPid()).orElse(null);
            }

            return secDbUserRepository.save(data);
        } catch (Exception e) {
            // Remove auto-generated pid if there was an error
            if (generatedPid) {
                data.setPid(null);
            }
            System.err.println("ERROR: SecDbUserService.create(data) Caught and rethrew " + e.getClass().getCanonicalName() +
                " while creating SecDbUser instance with pid: " +
                (data.getPid() != null ? data.getPid().asString() : "null") + " - " + e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbUser update(SecDbUser data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new IllegalArgumentException("Cannot update SecDbUser with null primary identifier (pid)");
        }

        // Check if the entity exists
        SecDbUser existing = secDbUserRepository.findById(data.getPid())
            .orElseThrow(() -> new NoResultException("SecDbUser with pid " + data.getPid() + " does not exist"));

        // Update fields (except pid, createdAt)
        existing.setUsername(data.getUsername());
        existing.setEmail(data.getEmail());
        existing.setMemberDeptCode(data.getMemberDeptCode());
        // ... update other fields as needed ...
        existing.setUpdatedAt(LocalDateTime.now());

        return secDbUserRepository.save(existing);
    }
}