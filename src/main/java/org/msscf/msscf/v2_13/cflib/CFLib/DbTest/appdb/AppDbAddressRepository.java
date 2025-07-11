package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface AppDbAddressRepository extends JpaRepository<AppDbAddress, CFLibDbKeyHash256> {
    public List<AppDbAddress> findByRefUID(CFLibDbKeyHash256 refUID);
}
