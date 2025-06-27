package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

import jakarta.persistence.*;

@Entity
@Table(name = "sec_sess", schema = "secdb")
public class SecDbSession {
    @Id
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 pid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "secuser_pid", referencedColumnName = "pid")
    private SecDbUser secUser;

    // ... other fields ...

    public SecDbSession() {}

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser) {
        this.pid = pid;
        this.secUser = secUser;
    }

    public CFLibDbKeyHash256 getPid() {
        return pid;
    }

    public void setPid(CFLibDbKeyHash256 pid) {
        this.pid = pid;
    }

    public SecDbUser getSecUser() {
        return secUser;
    }

    public void setSecUser(SecDbUser secUser) {
        this.secUser = secUser;
    }
}
