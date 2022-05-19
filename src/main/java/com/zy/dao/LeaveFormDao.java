package com.zy.dao;

import com.zy.entity.LeaveForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LeaveFormDao {

    public void insert(LeaveForm form);


    public List<Map>  selectByParams(@Param("pd_state")String pdState,@Param("pd_operator_id") Long operatorId);


    public LeaveForm selectById(Long formId);


    public void update(LeaveForm leaveForm);

}
