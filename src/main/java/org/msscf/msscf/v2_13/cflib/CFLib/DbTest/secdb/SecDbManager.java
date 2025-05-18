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

    @Column(name = "title", length = TITLE_SIZE)
    private String Title;

    @Column(name = "deptcode", length = DEPARTMENT_CODE_SIZE)
    private String DepartmentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdeptof")
    private SecDbManager SubDepartmentOf;

    @OneToMany(mappedBy = "SubDepartmentOf", fetch = FetchType.LAZY)
    private Set<SecDbManager> Departments = new HashSet<>();

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getDepartmentCode() {
        return DepartmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.DepartmentCode = departmentCode;
    }

    public SecDbManager getSubDepartmentOf() {
        return SubDepartmentOf;
    }

    public void setSubDepartmentOf(SecDbManager subDepartmentOf) {
        this.SubDepartmentOf = subDepartmentOf;
    }

    public Set<SecDbManager> getDepartments() {
        return Departments;
    }

    public void setDepartments(Set<SecDbManager> departments) {
        this.Departments = departments;
    }

    public void addDepartment(SecDbManager department) {
        if (department != null) {
            Departments.add(department);
            department.setSubDepartmentOf(this);
        }
    }

    public void removeDepartment(SecDbManager department) {
        if (department != null) {
            Departments.remove(department);
            department.setSubDepartmentOf(null);
        }
    }
}
