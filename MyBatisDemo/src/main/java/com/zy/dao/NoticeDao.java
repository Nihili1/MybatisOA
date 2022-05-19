package com.zy.dao;

import com.zy.entity.Notice;
import com.zy.entity.ProcessDispose;

import java.util.List;

public interface NoticeDao {

    public void insert(Notice notice);


    public List<Notice> selectByReceiverId(Long receiverId);
}
