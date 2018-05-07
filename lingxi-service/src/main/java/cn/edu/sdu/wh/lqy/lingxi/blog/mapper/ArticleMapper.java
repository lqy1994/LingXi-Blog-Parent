package cn.edu.sdu.wh.lqy.lingxi.blog.mapper;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ArchiveBo;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleExample;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ArticleMapper extends BaseMapper<Article> {

    long countByExample(ArticleExample example);

    int deleteByExample(ArticleExample example);

    int deleteByPrimaryKey(Integer id);

    int insertNewArticle(Article article);

    int insertSelective(Article record);

    List<Article> selectByExampleWithBLOBs(ArticleExample example);

    List<Article> selectById(String id);

    List<Article> selectByExample(ArticleExample example);

    Article selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Article record, @Param("example") ArticleExample example);

    int updateByExampleWithBLOBs(@Param("record") Article record, @Param("example") ArticleExample example);

    int updateByExample(@Param("record") Article record, @Param("example") ArticleExample example);

    int updateByPrimaryKeySelective(Article record);

    int updateByPrimaryKeyWithBLOBs(Article record);

    int updateByPrimaryKey(Article record);

    List<ArchiveBo> findReturnArchiveBo();

    List<Article> findByCatalog(Integer mid);

    List<Article> selectByTypeAndStatus(@Param("type") String type, @Param("status") String status);

}