package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.LogMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Log;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.LogVoExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ICommentService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ILogService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.DateKit;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//@Service(interfaceClass = ILogService.class)
public class LogServiceImpl implements ILogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    @Autowired
    private LogMapper logMapper;

    @Override
    public void insertLog(Log log) {
        logMapper.insert(log);
    }

    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        Log logs = new Log();
        logs.setAction(action);
        logs.setData(data);
        logs.setIp(ip);
        logs.setAuthorId(authorId);
        logs.setCreated(DateKit.getCurrentUnixTime());
        logMapper.insert(logs);
    }

    @Override
    public List<Log> getLogs(int page, int limit) {
        LOGGER.debug("Enter getLogs method:page={},linit={}",page,limit);
        if (page <= 0) {
            page = 1;
        }
        if (limit < 1 || limit > WebConstant.MAX_POSTS) {
            limit = 10;
        }
        LogVoExample logVoExample = new LogVoExample();
        logVoExample.setOrderByClause("id desc");
        PageHelper.startPage((page - 1) * limit, limit);
        List<Log> logs = logMapper.selectByExample(logVoExample);
        LOGGER.debug("Exit getLogs method");
        return logs;
    }
}
