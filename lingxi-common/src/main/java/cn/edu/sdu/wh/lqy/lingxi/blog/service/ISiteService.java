package cn.edu.sdu.wh.lqy.lingxi.blog.service;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ArchiveBo;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.BackResponseBo;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.StatisticsBo;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Comment;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.MetaDto;

import java.util.List;

/**
 * 站点服务
 *
 */
public interface ISiteService {


    /**
     * 最新收到的评论
     *
     * @param limit
     * @return
     */
    List<Comment> recentComments(int limit);

    /**
     * 最新发表的文章
     *
     * @param limit
     * @return
     */
    List<Article> recentContents(int limit);

    /**
     * 查询一条评论
     * @param coid
     * @return
     */
    Comment getComment(Integer coid);

    /**
     * 系统备份
     * @param bk_type
     * @param bk_path
     * @param fmt
     * @return
     */
    BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception;


    /**
     * 获取后台统计数据
     *
     * @return
     */
    StatisticsBo getStatistics();

    /**
     * 查询文章归档
     *
     * @return
     */
    List<ArchiveBo> getArchives();

    /**
     * 获取分类/标签列表
     * @return
     */
    List<MetaDto> metas(String type, String orderBy, int limit);

}
