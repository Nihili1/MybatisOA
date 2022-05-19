package com.zy.dao;

import com.zy.entity.User;
import com.zy.utils.MyBatisUtils;

import java.util.function.Function;

public class UserDao {

    /**
     *  数据库操作
     */

    public User selectByUserName(String username){
        User user = (User) MyBatisUtils.executeQuery(sqlSession -> sqlSession.selectOne("usermanager.selectByUserName", username));
            return user;


    }


}
