package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import java.util.Arrays;

import org.hibernate.annotations.UpdateTimestamp;
import org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb.SecDbUser;
import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;

@Entity
@Table(name = "data_blob")
public class AppDbDataBlob {

    public static final int DATANAME_SIZE = 1023;
    public static final int DATABLOB_SIZE = 1024 * 1024; // 1 MB

    @Id
    @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true, length = CFLibDbKeyHash256.HASH_LENGTH))
    private CFLibDbKeyHash256 PId;

    @Column(name = "dataname", nullable = false, unique = true, length = DATANAME_SIZE)
    private String dataname;

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

    @Column(name = "datablob", nullable = true, unique = false, length = DATABLOB_SIZE)
    private byte[] datablob;
    
    public CFLibDbKeyHash256 getPId() {
        return PId;
    }

    public void setPId(CFLibDbKeyHash256 PId) {
        this.PId = PId;
    }

    public String getDataname() {
        return dataname;
    }

    public void setDataname(String dataname) {
        this.dataname = dataname;
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

    public byte[] getDatablob() {
        return datablob;
    }

    public void setDatablob(byte[] datablob) {
        this.datablob = datablob;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppDbDataBlob that = (AppDbDataBlob) o;
        return 0 == this.PId.compareTo(that.PId) &&
                0 == this.dataname.compareTo(that.dataname) &&
                0 == Arrays.compare(datablob, that.datablob);
    }

    @Override
    public int hashCode() {
        int hc = PId == null ? 0 : PId.hashCode();
        hc = 31 * hc + (dataname == null ? 0 : dataname.hashCode());
        hc = 31 * hc + Arrays.hashCode(datablob);
        return hc;
    }
}
