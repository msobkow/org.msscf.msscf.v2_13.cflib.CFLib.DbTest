package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

@PersistenceContext(unitName = "AppDbPU")
public interface IAppDbAddressService {

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public AppDbAddress find(EntityManager em, CFLibDbKeyHash256 pid);

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public AppDbAddress findByRefUIDName(EntityManager em, CFLibDbKeyHash256 refUID, String name);

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = NoResultException.class)
    public List<AppDbAddress> findByRefUID(EntityManager em, CFLibDbKeyHash256 refUID);

    @Transactional(Transactional.TxType.REQUIRED)
    public AppDbAddress create(EntityManager em, AppDbAddress data);

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = NoResultException.class)
    public AppDbAddress update(EntityManager em, AppDbAddress data);
}
