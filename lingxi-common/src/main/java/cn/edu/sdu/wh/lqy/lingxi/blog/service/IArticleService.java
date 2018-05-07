package cn.edu.sdu.wh.lqy.lingxi.blog.service;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.BrowseSearch;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.ArticleDTO;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.search.ServiceMultiResult;
import com.github.pagehelper.PageInfo;

public interface IArticleService  {

    /**
     * 发布文章
     * @param contents
     */
    String publish(Article contents);

    /**
     *查询文章返回多条数据
     * @param offset 当前页
     * @param limit 每页条数
     * @return Article
     */
    PageInfo<Article> getArticles(Integer offset, Integer limit);


    /**
     * 根据id获取文章
     *
     * @param id id
     * @return Article
     */
    Article getArticle(String id);

    /**
     * 根据主键更新
     * @param article article
     */
    void updateContentByCid(Article article);


    /**
     * 查询分类/标签下的文章归档
     * @param mid mid
     * @param page page
     * @param limit limit
     * @return Article
     */
    PageInfo<Article> getArticles(Integer mid, int page, int limit);

    /**
     * 搜索、分页
     * @param keyword keyword
     * @param page page
     * @param limit limit
     * @return Article
     */
    PageInfo<Article> getArticles(String keyword, Integer page, Integer limit);


    /**
     * @param commentVoExample
     * @param page
     * @param limit
     * @return
     */
    PageInfo<Article> getArticlesWithpage(ArticleExample commentVoExample, Integer page, Integer limit);
    /**
     * 根据文章id删除
     * @param cid
     */
    String deleteByCid(Integer cid);

    /**
     * 编辑文章
     * @param contents
     */
    String updateArticle(Article contents);


    /**
     * 更新原有文章的category
     * @param ordinal
     * @param newCatefory
     */
    void updateCategory(String ordinal, String newCatefory);

    ServiceMultiResult<ArticleDTO> query(BrowseSearch browseSearch);

}
