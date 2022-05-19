package com.zy.service;

import com.zy.dao.EmployeeDao;
import com.zy.dao.LeaveFormDao;
import com.zy.dao.NoticeDao;
import com.zy.dao.ProcessDisposeDao;
import com.zy.entity.Employee;
import com.zy.entity.LeaveForm;
import com.zy.entity.Notice;
import com.zy.entity.ProcessDispose;
import com.zy.utils.BusinessException;
import com.zy.utils.MyBatisUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaveFormService {
    /**
     * 创建请假单
     *
     * @param form 客户端输入的请假单数据
     * @return 请假单对象
     */

    public LeaveForm createLeaveForm(LeaveForm form) {
        LeaveForm leaveForm = (LeaveForm) MyBatisUtils.executeUpdate(sqlSession -> {
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);

            // 提交请假表单
            Employee employee = employeeDao.selectById(form.getEmpId());

            //申请请假单，级别为8员工  -- 总经理
            if (employee.getLevel() == 8) {
                form.setState("approved");
            } else {
                form.setState("processing");
            }

            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);

            leaveFormDao.insert(form);
            //2.增加一条审批流程，说明表单已提交完成
            ProcessDisposeDao processDisposeDao = sqlSession.getMapper(ProcessDisposeDao.class);

            ProcessDispose pd = new ProcessDispose();
            pd.setFormId(form.getFormId());
            pd.setOperatorId(employee.getEmpId());
            pd.setAction("apply");
            pd.setCreateTime(new Date());
            pd.setOrderNo(1);
            pd.setState("complete");
            pd.setIsLast(0);
            processDisposeDao.insert(pd);


            // 级别为7以下，生成部门经理审批，请假时间大于72h, 需要生成总经理审批任务

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH时");

            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);


            if (employee.getLevel() < 7) {
                Employee dmanager = employeeDao.selectLeader(employee);
                ProcessDispose pd2 = new ProcessDispose();
                pd2.setFormId(form.getFormId());
                pd2.setOperatorId(dmanager.getEmpId());
                pd2.setAction("audit");
                pd2.setCreateTime(new Date());
                pd2.setOrderNo(2);
                pd2.setState("process");
                pd2.setIsLast(0);
                long difference = form.getEndTime().getTime() - form.getStartTime().getTime();
                float hours = difference / (1000 * 60 * 60) * 1f;
                if (hours >= BusinessConstants.MANAGER_AUDIT_HOURS) {
                    pd2.setIsLast(0);
                    processDisposeDao.insert(pd2);
                    Employee boss = employeeDao.selectLeader(dmanager);
                    ProcessDispose pd3 = new ProcessDispose();
                    pd3.setFormId(form.getFormId());
                    pd3.setOperatorId(boss.getEmpId());
                    pd3.setAction("audit");
                    pd3.setCreateTime(new Date());
                    pd3.setOrderNo(3);
                    pd3.setState("ready");
                    pd3.setIsLast(1);

                    processDisposeDao.insert(pd3);
                } else {
                    pd2.setIsLast(1);
                    processDisposeDao.insert(pd2);

                }
                //请假单已提交消息- 面向申请者
                String noticeContent = String.format("您的请假单申请[%s-%s]已经提交,请等待上级审批."
                        , sdf.format(form.getStartTime()), sdf.format(form.getEndTime()));

                noticeDao.insert(new Notice(employee.getEmpId(), noticeContent));

                //通知部门经理审批消息-面向审批人
                noticeContent = String.format("%s-%s提起请假申请[%s-%s],请尽快审批",
                        employee.getTitle(), employee.getEmpName(), sdf.format(form.getStartTime()), sdf.format(form.getEndTime()));

                noticeDao.insert(new Notice(dmanager.getEmpId(), noticeContent));


                //级别为7员工--部门经理 (上级:总经理)
            } else if (employee.getLevel() == 7) {
                Employee manager = employeeDao.selectLeader(employee);
                ProcessDispose pd4 = new ProcessDispose();
                pd4.setFormId(form.getFormId());
                pd4.setOperatorId(manager.getEmpId());
                pd4.setAction("audit");
                pd4.setCreateTime(new Date());
                pd4.setOrderNo(2);
                pd4.setState("process");
                pd4.setIsLast(1);

                processDisposeDao.insert(pd4);


                //请假单已提交消息- 面向申请者
                String noticeContent = String.format("您的请假单申请[%s-%s]已经提交,请等待上级审批."
                        , sdf.format(form.getStartTime()), sdf.format(form.getEndTime()));

                noticeDao.insert(new Notice(employee.getEmpId(), noticeContent));

                //通知总经理审批消息-面向审批人
                noticeContent = String.format("%s-%s提起请假申请[%s-%s],请尽快审批",
                        employee.getTitle(), employee.getEmpName(), sdf.format(form.getStartTime()), sdf.format(form.getEndTime()));

                noticeDao.insert(new Notice(manager.getEmpId(), noticeContent));

                ///级别为8员工--总经理
            } else if (employee.getLevel() == 8) {
                ProcessDispose pd5 = new ProcessDispose();
                pd5.setFormId(form.getFormId());
                pd5.setOperatorId(employee.getEmpId());
                pd5.setAction("audit");
                pd5.setResult("approved");
                pd5.setReason("自动通过");
                pd5.setCreateTime(new Date());
                pd5.setOrderNo(2);
                pd5.setState("complete");
                pd5.setIsLast(1);

                processDisposeDao.insert(pd5);

                //请假单已提交消息- 面向申请者（总经理）
                String noticeContent = String.format("您的请假单申请[%s-%s]已经自动通过."
                        , sdf.format(form.getStartTime()), sdf.format(form.getEndTime()));

                noticeDao.insert(new Notice(employee.getEmpId(), noticeContent));

            }
            return form;
        });
        return leaveForm;
    }


    /**
     * 获取指定任务状态及指定经办人对应的请假单列表
     *
     * @param pdState
     * @param operatorId
     * @return
     */
    public List<Map> getLeaveFormList(String pdState, Long operatorId) {
        return (List<Map>) MyBatisUtils.executeQuery(sqlSession -> {
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);

            List<Map> formList = leaveFormDao.selectByParams(pdState, operatorId);

            return formList;
        });
    }


    /**
     * 审批流程操作
     * 1.无论 同意 / 驳回，当前任务状态变更为complete
     * 2.如果当前任务是最后一个节点，代表流程结束，更新请假单状态为对应的approved/refused
     * 3.若当前任务不是最后一个节点，并且审批通过，把下一个节点状态从ready 变为process
     * 4.若当前任务不是最后一个节点，并且审批驳回，则后续所有任务状态变为cancel，请假单状态变为refused
     *
     * @param formId
     * @param operatorId
     * @param result
     * @param reason
     */
    public void audit(Long formId, Long operatorId, String result, String reason) {
        MyBatisUtils.executeUpdate(sqlSession -> {
            /**
             *1. 无论同意 / 驳回，状态更改为complete
             */

            ProcessDisposeDao processDisposeDao = sqlSession.getMapper(ProcessDisposeDao.class);

            List<ProcessDispose> disposeList = processDisposeDao.selectByFormId(formId);

            if (disposeList.size() == 0) {
                throw new BusinessException("PD001", "无效审批流程");
            }

            //获取当前任务
            List<ProcessDispose> processList = disposeList.stream().filter(p -> p.getOperatorId() == operatorId && p.getState().equals("process")).collect(Collectors.toList());
            ProcessDispose processDispose = null;
            if (processList.size() == 0) {
                throw new BusinessException("PD002", "未找到待处理任务");
            } else {
                processDispose = processList.get(0);
                processDispose.setState("complete");
                processDispose.setResult(result);
                processDispose.setReason(reason);
                processDispose.setAuditTime(new Date());
                processDisposeDao.update(processDispose);
            }

            /**
             *  2. 若当前任务是最后一个节点，表示流程结束，更新请假单状态为对应的approved / refused
             */

            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            LeaveForm form = leaveFormDao.selectById(formId);

            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH时");
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);

            Employee employee = employeeDao.selectById(form.getEmpId());  //申请表单人信息

            Employee operator = employeeDao.selectById(operatorId);  //任务经办人信息

            if (processDispose.getIsLast() == 1) {
                processDispose.setState(result);
                leaveFormDao.update(form);

                String noticeResult=null;
                if (result.equals("approved")) {
                     noticeResult ="批准";
                }else if(result.equals("refused")){
                     noticeResult ="拒绝";
                }

                //消息1，通知表单提交人
                String noticeContent = String.format("你的请假申请[%s-%s]%s%s已%s,[审批意见:%s],审批流程结束"
                ,sdf.format(form.getStartTime()),sdf.format(form.getEndTime()),operator.getTitle(),operator.getEmpName(),noticeResult,reason);

                noticeDao.insert(new Notice(form.getEmpId(),noticeContent));


                //消息2，通知部门经理审批人
                noticeContent = String.format("%s-%s提起申请[%s-%s]您已%s,[审批意见:%s],审批流程已结束"
                        ,employee.getTitle(),employee.getEmpName(),sdf.format(form.getStartTime()),sdf.format(form.getEndTime()),noticeResult,reason);

                noticeDao.insert(new Notice(operator.getEmpId(),noticeContent));


                /**
                 * 3.如果当前任务不是最后一个节点且审批通过-approved，下一个节点状态从ready 变为process
                 */
            } else {
                //后续任务节点
                List<ProcessDispose> readyList = disposeList.stream().filter(p -> p.getState().equals("ready")).collect(Collectors.toList());
                if (result.equals("approved")) {
                    ProcessDispose readyProcess = readyList.get(0);
                    readyProcess.setState("process");
                    processDisposeDao.update(readyProcess);


                    //消息1，通知表单提交人
                    String noticeContent1 = String.format("你的请假申请[%s-%s]%s%s已批准,[审批意见:%s],请继续等待上级审批"
                            ,sdf.format(form.getStartTime()),sdf.format(form.getEndTime()),operator.getTitle(),operator.getEmpName(),reason);

                    noticeDao.insert(new Notice(form.getEmpId(),noticeContent1));

                    //消息2，通知总经理审批人
                    String   noticeContent2 = String.format("%s-%s提起申请[%s-%s],请尽快审批"
                            ,employee.getTitle(),employee.getEmpName(),sdf.format(form.getStartTime()),sdf.format(form.getEndTime()));

                    noticeDao.insert(new Notice(readyProcess.getOperatorId(),noticeContent2));

                    //消息3，通知部门经理审批人
                    String  noticeContent3 = String.format("%s-%s提起申请[%s-%s]您已批准,审批意见:%s,申请转职上级领导审批"
                            ,employee.getTitle(),employee.getEmpName(),sdf.format(form.getStartTime()),sdf.format(form.getEndTime()),reason);

                    noticeDao.insert(new Notice(operator.getEmpId(),noticeContent3));



                    /**
                     * 4. 如果当前任务不是最后一个节点，并且审批驳回，后续所有任务状态变为cancel, 请假单状态变为refused
                     */

                } else if (result.equals("refused")) {
                    for (ProcessDispose p : readyList) {
                        p.setState("cancel");
                        processDisposeDao.update(p);
                    }

                    form.setState("refused");

                    leaveFormDao.update(form);

                }


            }

            return null;
        });

    }


}
