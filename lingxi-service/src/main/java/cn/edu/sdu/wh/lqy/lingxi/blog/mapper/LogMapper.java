package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Log;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.LogVoExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LogMapper {
    long countByExample(LogVoExample example);

    int deleteByExample(LogVoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Log record);

    int insertSelective(Log record);

    List<Log> selectByExample(LogVoExample example);

    Log selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Log record, @Param("example") LogVoExample example);

    int updateByExample(@Param("record") Log record, @Param("example") LogVoExample example);

    int updateByPrimaryKeySelective(Log record);

    int updateByPrimaryKey(Log record);
}