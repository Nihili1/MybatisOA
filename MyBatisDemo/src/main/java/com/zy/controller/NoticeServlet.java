package com.zy.controller;

import com.alibaba.fastjson.JSON;
import com.zy.entity.Notice;
import com.zy.entity.User;
import com.zy.service.NoticeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NoticeServlet",urlPatterns = "/notice/list")
public class NoticeServlet extends HttpServlet {

    private   NoticeService noticeService=new NoticeService();


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=utf-8");

       User user =(User)request.getSession().getAttribute("login_user");

        List<Notice> noticeList = noticeService.getNoticeList(user.getEmpId());


        Map result=new HashMap();

        result.put("code","0");
        result.put("msg","");
        result.put("count",noticeList.size());
        result.put("data",noticeList);

        String json = JSON.toJSONString(result);



        response.getWriter().println(json);
    }
}
