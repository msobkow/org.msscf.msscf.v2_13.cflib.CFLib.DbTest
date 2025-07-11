package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import java.util.List;
import java.util.Optional;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecDbManagerRepository extends JpaRepository<SecDbManager, CFLibDbKeyHash256> {
    List<SecDbManager> findByEmail(String email);
    List<SecDbManager> findByMemberDeptCode(String memberDeptCode);
}
