package com.zy.dao;

import com.zy.entity.Employee;
import org.apache.ibatis.annotations.Param;

public interface EmployeeDao {
     public Employee selectById(Long empId);

     public Employee selectLeader(@Param("emp") Employee employee);
}
