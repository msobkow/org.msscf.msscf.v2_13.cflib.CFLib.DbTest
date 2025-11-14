/*
 *	MSS Code Factory CFLib 2.13 Database Testing
 *
 *	Copyright (C) 2016-2025 Mark Stephen Sobkow (mailto:mark.sobkow@gmail.com)
 *	
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see &lt;https://www.gnu.org/licenses/&gt;.
 *	
 *	If you wish to modify and use this code without publishing your changes,
 *	or integrate it with proprietary code, please contact Mark Stephen Sobkow
 *	for a commercial license at mark.sobkow@gmail.com
 */
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
import jakarta.persistence.NoResultException;

@Service("SecDbSessionService")
public class SecDbSessionService {

    @Autowired
    @Qualifier("secEntityManagerFactory")
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
            return null;
        }
        SecDbSession probe = new SecDbSession();
        probe.setSecUser(user);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withMatcher("secuser_pid", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<SecDbSession> example = Example.of(probe, matcher);

        return secDbSessionRepository.findAll(example);
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
                (data.getPid() != null ? data.getPid().toString() : "null") + " - " + e.getMessage());
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
