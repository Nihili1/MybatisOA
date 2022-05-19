package com.zy.controller;

import com.alibaba.fastjson.JSON;
import com.zy.entity.User;
import com.zy.service.UserService;
import com.zy.utils.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = "/checkLogin")
public class LoginServlet extends HttpServlet {

    Logger logger= LoggerFactory.getLogger(LoginServlet.class);
    private UserService userService = new UserService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
       // 接收用户输入
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String,Object> result=new HashMap<>();

        try {
            // 调用业务逻辑
            User user = userService.checkLogin(username, password);
            HttpSession session = request.getSession();
            session.setAttribute("login_user",user);
            result.put("code","0");  // 0 表示成功
            result.put("message","success");
            result.put("redirect_url","/index");

        } catch (BusinessException e) {
             logger.error(e.getMessage(),e);
             result.put("code",e.getExceptionCode());
             result.put("message",e.getMessage());
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            result.put("code",e.getClass().getSimpleName());
            result.put("message",e.getMessage());
        }
        //返回对应结果到客户端

        String  json= JSON.toJSONString(result);

        response.getWriter().println(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
