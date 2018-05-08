package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.ArticleMetaMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleMeta;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleMetaExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleMateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//@Service(interfaceClass = IArticleMateService.class)
public class ArticleMateServiceImpl implements IArticleMateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleMateServiceImpl.class);

    @Autowired
    private ArticleMetaMapper articleMetaMapper;

    @Override
    public void deleteById(Integer cid, Integer mid) {
        ArticleMetaExample articleMetaExample = new ArticleMetaExample();
        ArticleMetaExample.Criteria criteria = articleMetaExample.createCriteria();
        if (cid != null) {
            criteria.andCidEqualTo(cid);
        }
        if (mid != null) {
            criteria.andMidEqualTo(mid);
        }
        articleMetaMapper.deleteByExample(articleMetaExample);
    }

    @Override
    public List<ArticleMeta> getArticleMetaById(Integer cid, Integer mid) {
        ArticleMetaExample articleMetaExample = new ArticleMetaExample();
        ArticleMetaExample.Criteria criteria = articleMetaExample.createCriteria();
        if (cid != null) {
            criteria.andCidEqualTo(cid);
        }
        if (mid != null) {
            criteria.andMidEqualTo(mid);
        }
        return articleMetaMapper.selectByExample(articleMetaExample);
    }

    @Override
    public void insertArticleMeta(ArticleMeta articleMeta) {
        articleMetaMapper.insert(articleMeta);
    }

    @Override
    public Long countById(Integer cid, Integer mid) {
        LOGGER.debug("Enter countById method:cid={},mid={}",cid,mid);
        ArticleMetaExample articleMetaExample = new ArticleMetaExample();
        ArticleMetaExample.Criteria criteria = articleMetaExample.createCriteria();
        if (cid != null) {
            criteria.andCidEqualTo(cid);
        }
        if (mid != null) {
            criteria.andMidEqualTo(mid);
        }
        long num = articleMetaMapper.countByExample(articleMetaExample);
        LOGGER.debug("Exit countById method return num={}",num);
        return num;
    }
}
