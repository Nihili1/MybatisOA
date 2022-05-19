package com.zy.service;

import com.zy.dao.EmployeeDao;
import com.zy.entity.Employee;
import com.zy.utils.MyBatisUtils;

public class EmployeeService {

    public Employee selectById(Long empId){

        return (Employee)MyBatisUtils.executeQuery(sqlSession -> {
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
            Employee emp = employeeDao.selectById(empId);

             return emp;

        });


    }
}
