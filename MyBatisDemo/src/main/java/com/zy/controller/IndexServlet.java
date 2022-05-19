package com.zy.controller;

import com.zy.entity.Department;
import com.zy.entity.Employee;
import com.zy.entity.Node;
import com.zy.entity.User;
import com.zy.service.DepartmentService;
import com.zy.service.EmployeeService;
import com.zy.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IndexServlet", urlPatterns = "/index")
public class IndexServlet extends HttpServlet {
    private UserService userService=new UserService();

    private EmployeeService employeeService=new EmployeeService();

    private DepartmentService departmentService=new DepartmentService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    //前端 window.location.href 动态跳转通过 get方式进行
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        // 获得当前登录用户对象
        User user =(User) session.getAttribute("login_user");

        //对应的拥有功能模块
        List<Node> nodes = userService.selectNodeByUserId(user.getUserId());
        // 获得当前用户所对应员工对象
        Employee employee = employeeService.selectById(user.getEmpId());

        //获得所在部门
        Department department = departmentService.selectById(employee.getDeptId());

        //放入请求属性
        request.setAttribute("nodeList",nodes);
        // 放入会话属性
        session.setAttribute("currentEmp",employee);

        session.setAttribute("currentDept",department);

        request.getRequestDispatcher("/index.ftl").forward(request,response);

    }
}
