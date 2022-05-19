package com.zy.service;

import com.zy.dao.RbacDao;
import com.zy.dao.UserDao;
import com.zy.entity.Node;
import com.zy.entity.User;
import com.zy.utils.BusinessException;
import com.zy.utils.MD5Utils;

import java.util.List;

public class UserService {
    private UserDao userDao=new UserDao();

    private RbacDao rbacDao=new RbacDao();
    /**
     * 根据客户端输入进行登录校验
     * @param username
     * @param password
     * @return  UL404： 在尝试登录，用户名不存在 ；
     *             PL404：在尝试登录，密码错误
     */
    public User checkLogin(String username,String password){
        User user = userDao.selectByUserName(username);

        if(user==null){
             throw new BusinessException("UL404","用户名不存在");
        }

        String md5 = MD5Utils.MD5Digest(password, user.getCipher());
        if (! md5.equals(user.getPassword())){
            throw  new BusinessException("UP404","密码错误");
        }

        return user;

    }



    public List<Node> selectNodeByUserId(Long userId){

        List<Node> nodes = rbacDao.selectNodeByUserId(userId);
        return nodes;
    }


}
