package com.zy.service;

import com.zy.dao.NoticeDao;
import com.zy.entity.Notice;
import com.zy.utils.MyBatisUtils;

import java.util.List;

public class NoticeService {


    public List<Notice> getNoticeList(Long receiverId) {

        return (List) MyBatisUtils.executeQuery(sqlSession -> {
            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);


            List<Notice> notices = noticeDao.selectByReceiverId(receiverId);

            return notices;

        });


    }
}
