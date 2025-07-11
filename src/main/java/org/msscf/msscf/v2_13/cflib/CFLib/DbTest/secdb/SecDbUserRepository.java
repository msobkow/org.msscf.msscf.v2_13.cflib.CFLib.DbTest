package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecDbUserRepository extends JpaRepository<SecDbUser, CFLibDbKeyHash256> {
    List<SecDbUser> findByEmail(String email);
    List<SecDbUser> findByMemberDeptCode(String memberDeptCode);
}
