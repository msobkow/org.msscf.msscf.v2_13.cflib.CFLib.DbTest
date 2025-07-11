package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

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

import jakarta.persistence.NoResultException;

@Service("AppDbAddressService")
public class AppDbAddressService {

    @Autowired
    @Qualifier("appEntityManagerFactoryBean")
    private LocalContainerEntityManagerFactoryBean appEntityManagerFactoryBean;
    
    @Autowired
    private AppDbAddressRepository appDbAddressRepository;

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public AppDbAddress find(CFLibDbKeyHash256 pid) {
        return appDbAddressRepository.findById(pid).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public List<AppDbAddress> findByRefUID(CFLibDbKeyHash256 refUID) {
        if (refUID == null || refUID.isNull()) {
            return new ArrayList<>();
        }
        return appDbAddressRepository.findByRefUID(refUID);
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public AppDbAddress findByRefUIDName(CFLibDbKeyHash256 refUID, String addressName) {
        if (refUID == null || refUID.isNull() || addressName == null || addressName.isEmpty()) {
            return null;
        }
        AppDbAddress probe = new AppDbAddress();
        probe.setRefUID(refUID);
        probe.setAddressName(addressName);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withMatcher("refuid, addrname", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<AppDbAddress> example = Example.of(probe, matcher);

        return appDbAddressRepository.findOne(example).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public AppDbAddress create(AppDbAddress data) {
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
            if (data.getPid() != null && appDbAddressRepository.existsById(data.getPid())) {
                return appDbAddressRepository.findById(data.getPid()).orElse(null);
            }

            return appDbAddressRepository.save(data);
        } catch (Exception e) {
            // Remove auto-generated pid if there was an error
            if (generatedPid) {
                data.setPid(null);
            }
            System.err.println("ERROR: AppDbAddressService.create(data) Caught and rethrew " + e.getClass().getCanonicalName() +
                " while creating AppDbAddress instance with pid: " +
                (data.getPid() != null ? data.getPid().asString() : "null") + " - " + e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NoResultException.class, transactionManager = "secTransactionManager")
    public AppDbAddress update(AppDbAddress data) {
        if (data == null) {
            return null;
        }
        if (data.getPid() == null || data.getPid().isNull()) {
            throw new IllegalArgumentException("Cannot update AppDbAddress with null primary identifier (pid)");
        }

        // Check if the entity exists
        AppDbAddress existing = appDbAddressRepository.findById(data.getPid())
            .orElseThrow(() -> new NoResultException("AppDbAddress with pid " + data.getPid() + " does not exist"));

        // Update fields (except pid, createdAt)
        existing.setAddressName(data.getAddressName());
        existing.setAddressApartment(data.getAddressApartment());
        existing.setAddressCity(data.getAddressCity());
        existing.setAddressContact(data.getAddressContact());
        existing.setAddressCountry(data.getAddressCountry());
        existing.setAddressPostalCode(data.getAddressPostalCode());
        existing.setAddressProvince(data.getAddressProvince());
        existing.setAddressStreet(data.getAddressStreet());
        existing.setAddressStreet2(data.getAddressStreet2());

        // ... update other fields as needed ...
        existing.setUpdatedAt(LocalDateTime.now());

        return appDbAddressRepository.save(existing);
    }
}
