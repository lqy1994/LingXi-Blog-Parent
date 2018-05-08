package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.exception.LingXiException;
import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.UserMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.UserExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IUserService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Service(interfaceClass = IUserService.class)
public class UserServiceImpl implements IUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Integer insertUser(User user) {
        Integer uid = null;
        if (StringUtils.isNotBlank(user.getUsername()) && StringUtils.isNotBlank(user.getEmail())) {
//            用户密码加密
            String encodePwd = TaleUtils.MD5encode(user.getUsername() + user.getPassword());
            user.setPassword(encodePwd);
            userMapper.insertSelective(user);
        }
        return user.getUid();
    }

    @Override
    public User queryUserById(Integer uid) {
        User user = null;
        if (uid != null) {
            user = userMapper.selectByPrimaryKey(uid);
        }
        return user;
    }

    @Override
    public User login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new LingXiException("用户名和密码不能为空");
        }
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        long count = userMapper.countByExample(example);
        if (count < 1) {
            throw new LingXiException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(username + password);
        criteria.andPasswordEqualTo(pwd);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() != 1) {
            throw new LingXiException("用户名或密码错误");
        }
        return users.get(0);
    }

    @Override
    @Transactional
    public void updateByUid(User user) {
        if (null == user || null == user.getUid()) {
            throw new LingXiException("user is null");
        }
        int i = userMapper.updateByPrimaryKeySelective(user);
        if (i != 1) {
            throw new LingXiException("update user by uid and retrun is not one");
        }
    }

    @Override
    public User getUserByName(String userName) {
        if (userName != null && !userName.equals("")) {
            return userMapper.selectByUserName(userName);
        }
        return null;
    }
}
