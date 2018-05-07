package cn.edu.sdu.wh.lqy.lingxi.blog.service;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleMeta;

import java.util.List;

public interface IArticleMateService {
    /**
     * 按住键删除
     * @param cid
     * @param mid
     */
    void deleteById(Integer cid, Integer mid);

    /**
     * 按主键统计条数
     * @param cid
     * @param mid
     * @return 条数
     */
    Long countById(Integer cid, Integer mid);


    /**
     * 保存對象
     * @param articleMeta
     */
    void insertVo(ArticleMeta articleMeta);

    /**
     * 根据id搜索
     * @param cid
     * @param mid
     * @return
     */
    List<ArticleMeta> getRelationshipById(Integer cid, Integer mid);
}
