package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

@Entity
@Table(name = "sec_sess", schema = "secdb")
@Transactional(Transactional.TxType.SUPPORTS)
@PersistenceContext(unitName = "SecDbPU")
public class SecDbSession {
    public final static int SESS_CREATE_INFO_LEN = 1024;
    public final static int SESS_TERMINATION_INFO_LEN = 1024;

    @Id
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private CFLibDbKeyHash256 pid;

    @ManyToOne(fetch = FetchType.LAZY)
    @AttributeOverrides({
        @AttributeOverride(name = "bytes", column = @Column(name = "secuser_pid", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    })
    private SecDbUser secUser;

    @Column(name = "sess_cr_info", nullable = false, updatable = false, length = SESS_CREATE_INFO_LEN)
    private String sessCreateInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sess_term_info", nullable = true, updatable = true, length = SESS_TERMINATION_INFO_LEN)
    private String sessTerminationInfo;

    @Column(name = "terminated_at", nullable = true, updatable = true)
    private LocalDateTime terminatedAt;

    @Autowired
    @Qualifier("secEntityManagerFactory")
    private static EntityManagerFactory secEntityManagerFactory;
  
    public SecDbSession() {}

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser) {
        this.pid = pid;
        this.secUser = secUser;
    }

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser, String sessCreateInfo) {
        this.pid = pid;
        this.secUser = secUser;
        this.sessCreateInfo = sessCreateInfo;
        this.createdAt = LocalDateTime.now();
        this.sessTerminationInfo = null;
        this.terminatedAt = null;
    }

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser, String sessCreateInfo, LocalDateTime createdAt) {
        this.pid = pid;
        this.secUser = secUser;
        this.sessCreateInfo = sessCreateInfo;
        this.createdAt = createdAt;
        this.sessTerminationInfo = null;
        this.terminatedAt = null;
    }

    public SecDbSession(CFLibDbKeyHash256 pid, SecDbUser secUser, String sessCreateInfo, LocalDateTime createdAt, String sessTerminationInfo, LocalDateTime terminatedAt) {
        this.pid = pid;
        this.secUser = secUser;
        this.sessCreateInfo = sessCreateInfo;
        this.createdAt = createdAt;
        this.sessTerminationInfo = sessTerminationInfo;
        this.terminatedAt = terminatedAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSessTerminationInfo() {
        return sessTerminationInfo;
    }

    public void setSessTerminationInfo(String sessTerminationInfo) {
        this.sessTerminationInfo = sessTerminationInfo;
    }

    public LocalDateTime getTerminatedAt() {
        return terminatedAt;
    }

    public void setTerminatedAt(LocalDateTime terminatedAt) {
        this.terminatedAt = terminatedAt;
    }
}
