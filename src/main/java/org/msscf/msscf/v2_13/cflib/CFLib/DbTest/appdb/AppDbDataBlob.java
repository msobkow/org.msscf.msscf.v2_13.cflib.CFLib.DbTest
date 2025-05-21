package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.appdb;

import java.util.Arrays;

import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

import jakarta.persistence.*;

@Entity
@Table(name = "data_blob")
public class AppDbDataBlob {

    @Id
    @AttributeOverride(name = "bytes", column = @Column(name = "pid", nullable = false, unique = true))
    private CFLibDbKeyHash256 PId;

    @Column(name = "dataname", nullable = false, unique = true)
    private String dataname;

    @Column(name = "datablob", nullable = true, unique = false)
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
