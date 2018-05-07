package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.ArticleMetaMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleMeta;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.RelationshipVoExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleMateService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleService;
import com.alibaba.dubbo.config.annotation.Service;
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
        RelationshipVoExample relationshipVoExample = new RelationshipVoExample();
        RelationshipVoExample.Criteria criteria = relationshipVoExample.createCriteria();
        if (cid != null) {
            criteria.andCidEqualTo(cid);
        }
        if (mid != null) {
            criteria.andMidEqualTo(mid);
        }
        articleMetaMapper.deleteByExample(relationshipVoExample);
    }

    @Override
    public List<ArticleMeta> getRelationshipById(Integer cid, Integer mid) {
        RelationshipVoExample relationshipVoExample = new RelationshipVoExample();
        RelationshipVoExample.Criteria criteria = relationshipVoExample.createCriteria();
        if (cid != null) {
            criteria.andCidEqualTo(cid);
        }
        if (mid != null) {
            criteria.andMidEqualTo(mid);
        }
        return articleMetaMapper.selectByExample(relationshipVoExample);
    }

    @Override
    public void insertVo(ArticleMeta articleMeta) {
        articleMetaMapper.insert(articleMeta);
    }

    @Override
    public Long countById(Integer cid, Integer mid) {
        LOGGER.debug("Enter countById method:cid={},mid={}",cid,mid);
        RelationshipVoExample relationshipVoExample = new RelationshipVoExample();
        RelationshipVoExample.Criteria criteria = relationshipVoExample.createCriteria();
        if (cid != null) {
            criteria.andCidEqualTo(cid);
        }
        if (mid != null) {
            criteria.andMidEqualTo(mid);
        }
        long num = articleMetaMapper.countByExample(relationshipVoExample);
        LOGGER.debug("Exit countById method return num={}",num);
        return num;
    }
}
