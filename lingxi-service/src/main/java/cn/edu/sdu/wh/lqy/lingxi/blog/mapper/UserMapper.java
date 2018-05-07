package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.UserVoExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {

    long countByExample(UserVoExample example);

    int deleteByExample(UserVoExample example);

    int deleteByPrimaryKey(Integer uid);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserVoExample example);

    User selectByPrimaryKey(Integer uid);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserVoExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserVoExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserName(String userName);
}