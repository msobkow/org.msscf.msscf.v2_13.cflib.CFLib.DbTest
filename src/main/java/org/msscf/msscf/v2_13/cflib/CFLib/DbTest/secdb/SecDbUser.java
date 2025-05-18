package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

import jakarta.persistence.*;

@Entity
@Table(name = "sec_user")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("0")
public class SecDbUser {

    @Id
    @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true))
    private CFLibDbKeyHash256 PId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_pid")
    private SecDbUser prev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_pid")
    private SecDbUser next;

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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecDbUser that = (SecDbUser) o;
        return 0 == this.PId.compareTo(that.PId) &&
                0 == this.username.compareTo(that.username) &&
                0 == this.email.compareTo(that.email) &&
                ((this.prev == null && that.prev == null) || 
                 (this.prev != null && that.prev != null && this.prev.PId.equals(that.prev.PId))) &&
                ((this.next == null && that.next == null) || 
                 (this.next != null && that.next != null && this.next.PId.equals(that.next.PId)));
    }

    @Override
    public int hashCode() {
        int hc = PId == null ? 0 : PId.hashCode();
        hc = 31 * hc + (username == null ? 0 : username.hashCode());
        hc = 31 * hc + (email == null ? 0 : email.hashCode());
        hc = 31 * hc + (prev == null ? 0 : prev.PId.hashCode());
        hc = 31 * hc + (next == null ? 0 : next.PId.hashCode());
        return hc;
    }
}
