package cn.edu.sdu.wh.lqy.lingxi.blog.service;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;

public interface IUserService {

    /**
     * 保存用户数据
     *
     * @param user 用户数据
     * @return 主键
     */

    Integer insertUser(User user);

    /**
     * 通过uid查找对象
     * @param uid
     * @return
     */
    User queryUserById(Integer uid);

    /**
     * 用戶登录
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * 根据主键更新user对象
     * @param user
     * @return
     */
    void updateByUid(User user);

    /**
     * 根据用户名获取用户
     * @param userName
     * @return
     */
    User getUserByName(String userName);

}
