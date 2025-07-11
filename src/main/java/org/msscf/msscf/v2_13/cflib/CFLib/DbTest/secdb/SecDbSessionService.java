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
import jakarta.persistence.NoResultException;

@Service("SecDbSessionService")
public class SecDbSessionService {

    @Autowired
    @Qualifier("secEntityManagerFactoryBean")
    private LocalContainerEntityManagerFactoryBean secEntityManagerFactoryBean;
    
    @Autowired
    private SecDbSessionRepository secDbSessionRepository;

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbSession find(CFLibDbKeyHash256 pid) {
        return secDbSessionRepository.findById(pid).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<SecDbSession> findByUser(SecDbUser user) {
        if (user == null || user.getPid() == null || user.getPid().isNull()) {
            return new ArrayList<>();
        }
        return secDbSessionRepository.findByUser(user);

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbSession create(SecDbSession data) {
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

            // Check if already exists
            if (data.getPid() != null && secDbSessionRepository.existsById(data.getPid())) {
                return secDbSessionRepository.findById(data.getPid()).orElse(null);
            }

            return secDbSessionRepository.save(data);
        } catch (Exception e) {
            // Remove auto-generated pid if there was an error
            if (generatedPid) {
                data.setPid(null);
            }
            System.err.println("ERROR: SecDbSessionService.create(data) Caught and rethrew " + e.getClass().getCanonicalName() +
                " while creating SecDbSession instance with pid: " +
                (data.getPid() != null ? data.getPid().asString() : "null") + " - " + e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public SecDbSession update(SecDbSession data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new IllegalArgumentException("Cannot update SecDbSession with null primary identifier (pid)");
        }

        // Check if the entity exists
        SecDbSession existing = secDbSessionRepository.findById(data.getPid())
            .orElseThrow(() -> new NoResultException("SecDbSession with pid " + data.getPid() + " does not exist"));

        // Update fields (except pid, createdAt)
        existing.setSessTerminationInfo(data.getSessTerminationInfo());
        existing.setTerminatedAt(data.getTerminatedAt());

        return secDbSessionRepository.save(existing);
    }
}
