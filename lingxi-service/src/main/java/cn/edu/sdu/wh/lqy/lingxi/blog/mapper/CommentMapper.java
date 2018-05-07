package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Comment;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.CommentVoExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommentMapper {

    long countByExample(CommentVoExample example);

    int deleteByExample(CommentVoExample example);

    int deleteByPrimaryKey(Integer coid);

    int insert(Comment record);

    int insertSelective(Comment record);

    List<Comment> selectByExampleWithBLOBs(CommentVoExample example);

    List<Comment> selectByExample(CommentVoExample example);

    Comment selectByPrimaryKey(Integer coid);

    int updateByExampleSelective(@Param("record") Comment record, @Param("example") CommentVoExample example);

    int updateByExampleWithBLOBs(@Param("record") Comment record, @Param("example") CommentVoExample example);

    int updateByExample(@Param("record") Comment record, @Param("example") CommentVoExample example);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKeyWithBLOBs(Comment record);

    int updateByPrimaryKey(Comment record);
}