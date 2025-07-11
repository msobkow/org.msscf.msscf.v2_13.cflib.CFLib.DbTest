package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDbAddressRepository extends JpaRepository<AppDbAddress, CFLibDbKeyHash256> {

}
