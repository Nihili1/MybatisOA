package com.zy.dao;

import com.zy.entity.Department;
import com.zy.entity.Employee;

public interface DepartmentDao {
     public Department selectById(Long deptId);
}
