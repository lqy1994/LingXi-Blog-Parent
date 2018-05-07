package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Attach;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.AttachVoExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface AttachMapper {

    long countByExample(AttachVoExample example);

    int deleteByExample(AttachVoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Attach record);

    int insertSelective(Attach record);

    List<Attach> selectByExample(AttachVoExample example);

    Attach selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Attach record, @Param("example") AttachVoExample example);

    int updateByExample(@Param("record") Attach record, @Param("example") AttachVoExample example);

    int updateByPrimaryKeySelective(Attach record);

    int updateByPrimaryKey(Attach record);
}