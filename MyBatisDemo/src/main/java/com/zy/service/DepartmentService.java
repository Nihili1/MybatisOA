package com.zy.service;

import com.zy.dao.DepartmentDao;
import com.zy.entity.Department;
import com.zy.utils.MyBatisUtils;

public class DepartmentService {

    public Department selectById(Long deptId){
        Department department = (Department) MyBatisUtils.executeQuery(sqlSession ->
                sqlSession.getMapper(DepartmentDao.class).selectById(deptId));
        return department;
    }

}
