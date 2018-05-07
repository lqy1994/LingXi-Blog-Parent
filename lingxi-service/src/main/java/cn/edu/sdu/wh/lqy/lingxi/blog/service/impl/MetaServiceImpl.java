package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.exception.LingXiException;
import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.MetaMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleMeta;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Meta;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.MetaVoExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.MetaDto;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.TypeEnum;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleMateService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IMetaService;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service(interfaceClass = IMetaService.class)
public class MetaServiceImpl implements IMetaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaServiceImpl.class);

    @Autowired
    private MetaMapper metaMapper;

    @Autowired
    private IArticleMateService relationshipService;

    @Autowired
    private IArticleService articleService;

    @Override
    public MetaDto getMeta(String type, String name) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            return metaMapper.selectDtoByNameAndType(name, type);
        }
        return null;
    }

    @Override
    public Integer countMeta(Integer mid) {
        return metaMapper.countWithSql(mid);
    }

    @Override
    public List<Meta> getMetas(String types) {
        if (StringUtils.isNotBlank(types)) {
            MetaVoExample metaVoExample = new MetaVoExample();
            metaVoExample.setOrderByClause("sort desc, mid desc");
            metaVoExample.createCriteria().andTypeEqualTo(types);
            return metaMapper.selectByExample(metaVoExample);
        }
        return null;
    }

    @Override
    public List<MetaDto> getMetaList(String type, String orderby, int limit) {
        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.isBlank(orderby)) {
                orderby = "count desc, a.mid desc";
            }
            if (limit < 1 || limit > WebConstant.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderby);
            paraMap.put("limit", limit);
            return metaMapper.selectFromSql(paraMap);
        }
        return null;
    }

    @Override
    @Transactional
    public void delete(int mid) {
        Meta metas = metaMapper.selectByPrimaryKey(mid);
        if (null != metas) {
            String type = metas.getType();
            String name = metas.getName();

            metaMapper.deleteByPrimaryKey(mid);

            List<ArticleMeta> rlist = relationshipService.getRelationshipById(null, mid);
            if (null != rlist) {
                for (ArticleMeta r : rlist) {
                    Article contents = articleService.getArticle(String.valueOf(r.getCid()));
                    if (null != contents) {
                        Article temp = new Article();
                        temp.setId(r.getCid());
                        if (type.equals(TypeEnum.CATEGORY.getType())) {
                            temp.setCategories(reMeta(name, contents.getCategories()));
                        }
                        if (type.equals(TypeEnum.TAG.getType())) {
                            temp.setTags(reMeta(name, contents.getTags()));
                        }
                        articleService.updateContentByCid(temp);
                    }
                }
            }
            relationshipService.deleteById(null, mid);
        }
    }

    @Override
    @Transactional
    public void saveMeta(String type, String name, Integer mid) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            MetaVoExample metaVoExample = new MetaVoExample();
            metaVoExample.createCriteria().andTypeEqualTo(type).andNameEqualTo(name);
            List<Meta> metaVos = metaMapper.selectByExample(metaVoExample);
            Meta metas;
            if (metaVos.size() != 0) {
                throw new LingXiException("已经存在该项");
            } else {
                metas = new Meta();
                metas.setName(name);
                if (null != mid) {
                    Meta original = metaMapper.selectByPrimaryKey(mid);
                    metas.setMid(mid);
                    metaMapper.updateByPrimaryKeySelective(metas);
//                    更新原有文章的categories
                    articleService.updateCategory(original.getName(), name);
                } else {
                    metas.setType(type);
                    metaMapper.insertSelective(metas);
                }
            }
        }
    }

    @Override
    @Transactional
    public void saveMetas(Integer cid, String names, String type) {
        if (null == cid) {
            throw new LingXiException("项目关联id不能为空");
        }
        if (StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(names, ",");
            for (String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    private void saveOrUpdate(Integer cid, String name, String type) {
        MetaVoExample metaVoExample = new MetaVoExample();
        metaVoExample.createCriteria().andTypeEqualTo(type).andNameEqualTo(name);
        List<Meta> metaVos = metaMapper.selectByExample(metaVoExample);

        int mid;
        Meta metas;
        if (metaVos.size() == 1) {
            metas = metaVos.get(0);
            mid = metas.getMid();
        } else if (metaVos.size() > 1) {
            throw new LingXiException("查询到多条数据");
        } else {
            metas = new Meta();
            metas.setThumbnail(name);
            metas.setName(name);
            metas.setType(type);
            metaMapper.insertSelective(metas);
            mid = metas.getMid();
        }
        if (mid != 0) {
            Long count = relationshipService.countById(cid, mid);
            if (count == 0) {
                ArticleMeta relationships = new ArticleMeta();
                relationships.setCid(cid);
                relationships.setMid(mid);
                relationshipService.insertVo(relationships);
            }
        }
    }


    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for (String m : ms) {
            if (!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if (sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }

    @Override
    @Transactional
    public void saveMeta(Meta metas) {
        if (null != metas) {
            metaMapper.insertSelective(metas);
        }
    }

    @Override
    @Transactional
    public void update(Meta metas) {
        if (null != metas && null != metas.getMid()) {
            metaMapper.updateByPrimaryKeySelective(metas);
        }
    }
}
