package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleMeta;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleMetaExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ArticleMetaMapper {

    long countByExample(ArticleMetaExample example);

    int deleteByExample(ArticleMetaExample example);

    int deleteByPrimaryKey(ArticleMeta key);

    int insert(ArticleMeta record);

    int insertSelective(ArticleMeta record);

    List<ArticleMeta> selectByExample(ArticleMetaExample example);

    int updateByExampleSelective(@Param("record") ArticleMeta record, @Param("example") ArticleMetaExample example);

    int updateByExample(@Param("record") ArticleMeta record, @Param("example") ArticleMetaExample example);

}