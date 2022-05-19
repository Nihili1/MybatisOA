package com.zy.dao;

import com.zy.entity.ProcessDispose;

import java.util.List;

public interface ProcessDisposeDao {

    public void insert(ProcessDispose processDispose);

    public void update(ProcessDispose processDispose);

    public List<ProcessDispose> selectByFormId(Long formId);
}
