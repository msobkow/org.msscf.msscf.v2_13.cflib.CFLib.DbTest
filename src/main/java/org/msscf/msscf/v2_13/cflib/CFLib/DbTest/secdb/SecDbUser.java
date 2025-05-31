package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.hibernate.annotations.UpdateTimestamp;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;

@Entity
@Table(name = "sec_user")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("0")
public class SecDbUser implements Comparable<Object> {

    public static final int USERNAME_SIZE = 64;
    public static final int EMAIL_SIZE = 1023;

    @Id
    @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true))
    private CFLibDbKeyHash256 PId;

    @Column(name = "username", nullable = false, unique = true, length = USERNAME_SIZE)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = EMAIL_SIZE)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @CreatedBy
    @AttributeOverride(name = "bytes", column = @Column(name = "created_by", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    private CFLibDbKeyHash256 createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;

    @AttributeOverride(name = "bytes", column = @Column(name = "updated_by", nullable = false, unique = false, length = CFLibDbKeyHash256.HASH_LENGTH))
    private CFLibDbKeyHash256 updatedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_pid")
    private SecDbUser prev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_pid")
    private SecDbUser next;

    @Column(name = "member_deptcode", length = 32, nullable = true)
    private String memberDeptCode;

    public CFLibDbKeyHash256 getPId() {
        return PId;
    }

    public void setPId(CFLibDbKeyHash256 PId) {
        this.PId = PId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SecDbUser getPrev() {
        return prev;
    }

    public void setPrev(SecDbUser prev) {
        this.prev = prev;
    }

    public SecDbUser getNext() {
        return next;
    }

    public void setNext(SecDbUser next) {
        this.next = next;
    }
    
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CFLibDbKeyHash256 getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CFLibDbKeyHash256 createdBy) {
        this.createdBy = createdBy;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CFLibDbKeyHash256 getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(CFLibDbKeyHash256 updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getMemberDeptCode() {
        return memberDeptCode;
    }

    public void setMemberDeptCode(String memberDeptCode) {
        this.memberDeptCode = memberDeptCode;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return 1;
        SecDbUser that = (SecDbUser) o;
        int cmp = this.PId.compareTo(that.PId);
        if (cmp != 0) return cmp;
        cmp = this.username.compareTo(that.username);
        if (cmp != 0) return cmp;
        cmp = this.email.compareTo(that.email);
        if (cmp != 0) return cmp;

        // Compare memberDeptCode
        cmp = ((this.memberDeptCode == null && that.memberDeptCode == null) ? 0 :
               (this.memberDeptCode != null && that.memberDeptCode != null && this.memberDeptCode.equals(that.memberDeptCode)) ? 0 :
               (this.memberDeptCode == null ? -1 : (that.memberDeptCode == null ? 1 : this.memberDeptCode.compareTo(that.memberDeptCode))));
        if (cmp != 0) return cmp;

        // Compare prev and next users
        if (this.prev == null && that.prev == null && this.next == null && that.next == null) {
            return 0;
        }
        else if (this.prev == null && that.prev != null) {
            return -1;
        } else if (that.prev == null && this.prev != null) {
            return 1;
        }
        else if (this.next == null && that.next != null) {
            return -1;
        } else if (that.next == null && this.next != null) {
            return 1;
        }
        if (this.prev == null && that.prev == null) {
            return 0;
        } else if (this.prev == null) {
            return -1;
        } else if (that.prev == null) {
            return 1;
        } else {
            cmp = this.prev.PId.compareTo(that.prev.PId);
            if (cmp != 0) return cmp;
        }
        if (this.next == null && that.next == null) {
            return 0;
        } else if (this.next == null) {
            return -1;
        } else if (that.next == null) {
            return 1;
        } else {
            cmp = this.next.PId.compareTo(that.next.PId);
            return cmp;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecDbUser that = (SecDbUser) o;
        return 0 == this.PId.compareTo(that.PId) &&
                0 == this.username.compareTo(that.username) &&
                0 == this.email.compareTo(that.email) &&
                ((this.memberDeptCode == null && that.memberDeptCode == null) ||
                 (this.memberDeptCode != null && that.memberDeptCode != null && this.memberDeptCode.equals(that.memberDeptCode))) &&
                ((this.prev == null && that.prev == null) || 
                 (this.prev != null && that.prev != null && this.prev.PId.equals(that.prev.PId))) &&
                ((this.next == null && that.next == null) || 
                 (this.next != null && that.next != null && this.next.PId.equals(that.next.PId)));
    }

    @Override
    public final int hashCode() {
        int hc = PId == null ? 0 : PId.hashCode();
        hc = 31 * hc + (username == null ? 0 : username.hashCode());
        hc = 31 * hc + (email == null ? 0 : email.hashCode());
        hc = 31 * hc + (memberDeptCode == null ? 0 : memberDeptCode.hashCode());
        hc = 31 * hc + (prev == null ? 0 : prev.PId.hashCode());
        hc = 31 * hc + (next == null ? 0 : next.PId.hashCode());
        return hc;
    }
}
