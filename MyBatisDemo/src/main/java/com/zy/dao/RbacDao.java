package com.zy.dao;

import com.zy.entity.Node;
import com.zy.utils.MyBatisUtils;

import java.util.List;

public class RbacDao {
    public List<Node> selectNodeByUserId(Long userId){
       return  (List) MyBatisUtils.executeQuery(sqlSession -> sqlSession.selectList("rbacMapper.selectNodeByUserId",userId));
    }

}
