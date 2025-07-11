package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecDbManagerRepository extends JpaRepository<SecDbManager, CFLibDbKeyHash256> {

}
