package com.zy.controller;

import com.alibaba.fastjson.JSON;
import com.zy.entity.LeaveForm;
import com.zy.entity.User;
import com.zy.service.LeaveFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LeaveFormServlet" ,urlPatterns = "/leave/*")
public class LeaveFormServlet extends HttpServlet {

     private LeaveFormService leaveFormService=new LeaveFormService();

   private  Logger logger= LoggerFactory.getLogger(LeaveFormServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");


        String uri = request.getRequestURI();

        String methodName=uri.substring(uri.lastIndexOf("/")+1);

        if (methodName.equals("create")){
            this.create(request,response);
        }else if(methodName.equals("list")){
            this.getLeaveFormList(request,response);
        }else if(methodName.equals("audit")){
            this.audit(request,response);
        }

    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);

    }


    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();

        User user = (User)session.getAttribute("login_user");

        String formType = request.getParameter("formType");
        String strstartTime = request.getParameter("startTime");
        String strendTime = request.getParameter("endTime");
        String reason = request.getParameter("reason");

        Map result=new HashMap();


        try {
            LeaveForm form=new LeaveForm();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH");

            form.setEmpId(user.getEmpId());
            form.setFormType(Integer.parseInt(formType));
            form.setStartTime(sdf.parse(strstartTime));
            form.setEndTime(sdf.parse(strendTime));
            form.setReason(reason);
            form.setCreateTime(new Date());
            leaveFormService.createLeaveForm(form);
            result.put("code","0");
            result.put("message","success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("请假申请异常",e);
            result.put("code",e.getClass().getSimpleName());
            result.put("message",e.getMessage());
        }

        // 响应给客户端的json数据
        String json = JSON.toJSONString(result);
         response.getWriter().println(json);

    }

    /**
     *  得到请假列表
     * @param request
     * @param response
     * @throws IOException
     */
    private void getLeaveFormList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user =(User) request.getSession().getAttribute("login_user");
        List<Map> formList = leaveFormService.getLeaveFormList("process", user.getEmpId());

        Map result=new HashMap();

        //layui 属性
        result.put("code","0");
        result.put("msg","");
        result.put("count",formList.size());
        result.put("data",formList);

        String json = JSON.toJSONString(result);

        response.getWriter().println(json);

    }


    /**
     * 处理审批流程
     * @param request
     * @param response
     */
    private void audit(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String formId = request.getParameter("formId");
        String result = request.getParameter("result");
        String reason = request.getParameter("reason");

        User user =(User) request.getSession().getAttribute("login_user");
        Map  eMap=new HashMap();
        try {
            leaveFormService.audit(Long.parseLong(formId),user.getEmpId(),result,reason);
            eMap.put("code","0");
            eMap.put("message","success");
        }catch (Exception e){
            logger.error("请假单审批失败",e);
            eMap.put("code",e.getClass().getSimpleName());
            eMap.put("message",e.getMessage());
        }

        String json=JSON.toJSONString(eMap);

        response.getWriter().println(json);
    }
}
