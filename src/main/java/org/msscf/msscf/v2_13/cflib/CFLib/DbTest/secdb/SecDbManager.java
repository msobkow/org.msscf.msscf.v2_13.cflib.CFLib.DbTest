package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.secdb;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.HashSet;

// import org.msscf.msscf.v2_13.cflib.CFLib.dbutil.CFLibDbKeyHash256;

@Entity
@Table(
    name = "sec_mgr",
    indexes = {
        @Index(name = "sec_mgr_deptcode_ax", columnList = "deptcode", unique = true)
    }
)
@DiscriminatorValue("1")
public class SecDbManager extends SecDbUser {
    
    public static final int TITLE_SIZE = 64;
    public static final int DEPARTMENT_CODE_SIZE = 32;

    @Column(name = "title", length = TITLE_SIZE, nullable = false)
    private String title = "";

    @Column(name = "deptcode", length = DEPARTMENT_CODE_SIZE, nullable = false, unique = true)
    private String departmentCode = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdeptof")
    private SecDbManager subDepartmentOf;

    @OneToMany(mappedBy = "subDepartmentOf", fetch = FetchType.LAZY)
    private Set<SecDbManager> departments = new HashSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public SecDbManager getSubDepartmentOf() {
        return subDepartmentOf;
    }

    public void setSubDepartmentOf(SecDbManager subDepartmentOf) {
        this.subDepartmentOf = subDepartmentOf;
    }

    public Set<SecDbManager> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<SecDbManager> departments) {
        this.departments = departments;
    }

    public void addDepartment(SecDbManager department) {
        if (department != null) {
            departments.add(department);
            department.setSubDepartmentOf(this);
        }
    }

    public void removeDepartment(SecDbManager department) {
        if (department != null) {
            departments.remove(department);
            department.setSubDepartmentOf(null);
        }
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof SecDbManager)) return -1;
        SecDbManager other = (SecDbManager) o;
        int cmp = super.compareTo(other);
        if (cmp != 0) return cmp;
        cmp = (this.title == null && other.title == null) ? 0 : ((this.title != null && this.title.equals(other.title)) ? 0 : (this.title == null ? -1 : (other.title == null ? 1 : this.title.compareTo(other.title))));
        if (cmp != 0) return cmp;
        cmp = (this.departmentCode == null && other.departmentCode == null) ? 0 : ((this.departmentCode != null && this.departmentCode.equals(other.departmentCode)) ? 0 : (this.departmentCode == null ? -1 : (other.departmentCode == null ? 1 : this.departmentCode.compareTo(other.departmentCode))));
        if (cmp != 0) return cmp;
        cmp = (this.subDepartmentOf == null && other.subDepartmentOf == null) ? 0 : ((this.subDepartmentOf != null && other.subDepartmentOf != null && this.subDepartmentOf.getPId().equals((other.subDepartmentOf.getPId()))) ? 0 : (this.subDepartmentOf == null ? -1 : (other.subDepartmentOf == null ? 1 : this.subDepartmentOf.getPId().compareTo(other.subDepartmentOf.getPId()))));
        return cmp;
    }
}
