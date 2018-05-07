package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.ArticleMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.MetaMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.BrowseSearch;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.RangeValueBlock;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.ArticleDTO;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.TypeEnum;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.search.ServiceMultiResult;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleMateService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IMetaService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ISearchService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.DateKit;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.StringUtils;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.Tools;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


//@Service(interfaceClass = IArticleService.class)
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private MetaMapper metaMapper;

    @Autowired
    private IArticleMateService relationshipService;

    @Autowired
    private IMetaService metasService;
    @Autowired
    private ISearchService searchService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public String publish(Article article) {
        if (null == article) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(article.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(article.getContent())) {
            return "文章内容不能为空";
        }
        int titleLength = article.getTitle().length();
        if (titleLength > WebConstant.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        int contentLength = article.getContent().length();
        if (contentLength > WebConstant.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        article.setWordCnt(contentLength);
        if (null == article.getAuthorId()) {
            return "请登录后发布文章";
        }
        if (StringUtils.isNotBlank(article.getThumbnail())) {
            if (article.getThumbnail().length() < 5) {
                return "路径太短了";
            }
            if (!TaleUtils.isPath(article.getThumbnail())) return "您输入的路径不合法";
            ArticleExample articleExample = new ArticleExample();
            articleExample.createCriteria().andTypeEqualTo(article.getType()).andStatusEqualTo(article.getThumbnail());
            long count = articleMapper.countByExample(articleExample);
//            Integer count = articleMapper.selectCount(new EntityWrapper<>(article));
            if (count > 0) {
                return "该路径已经存在，请重新输入";
            }
        } else {
            article.setThumbnail(null);
        }

        article.setContent(article.getContent());

        int time = DateKit.getCurrentUnixTime();

        String string = valueOperations().get(WebConstant.ARTICLE_CURRENT_ID).toString();
        Integer curIdVal = Integer.valueOf(string.replaceAll("\"", ""));
        if (curIdVal != null) {
            curIdVal++;
            article.setId(curIdVal);
            valueOperations().set(WebConstant.ARTICLE_CURRENT_ID, String.valueOf(curIdVal), 60 * 60 * 24 * 30 * 12, TimeUnit.SECONDS);
        } else {
            article.setId(new Random().nextInt(1000000));
        }

        article.setCreated(time);
        article.setModified(time);
        article.setHits(0);
        article.setCommentsNum(0);

        String tags = article.getTags();
        String categories = article.getCategories();
        articleMapper.insertNewArticle(article);
        Integer artId = article.getId();
        metasService.saveMetas(artId, tags, TypeEnum.TAG.getType());
        metasService.saveMetas(artId, categories, TypeEnum.CATEGORY.getType());

        //同步索引数据
        searchService.index(artId);

        return WebConstant.SUCCESS_RESULT;
    }

    @Override
    public PageInfo<Article> getArticles(Integer offset, Integer limit) {
        LOGGER.debug("Begin getArticles: offset:{}, limit:{}.", offset, limit);
        ArticleExample example = new ArticleExample();
        example.setOrderByClause("created desc");
        example.createCriteria().andTypeEqualTo(TypeEnum.ARTICLE.getType()).andStatusEqualTo(TypeEnum.PUBLISH.getType());
        PageHelper.startPage(offset, limit);

        List<Article> data = articleMapper.selectByExampleWithBLOBs(example);

//        List<Article> data = articleMapper.selectByTypeAndStatus(TypeEnum.ARTICLE.getType(), TypeEnum.PUBLISH.getType());
        PageInfo<Article> pageInfo = new PageInfo<>(data);
        LOGGER.debug("Exit getArticles");
        return pageInfo;
    }

    @Override
    public Article getArticle(String id) {
        if (StringUtils.isNotBlank(id)) {
            if (Tools.isNumber(id)) {
//                Article article = articleMapper.selectById(Integer.valueOf(id));
                Article article = articleMapper.selectByPrimaryKey(Integer.valueOf(id));
                if (article != null) {
                    article.setHits(article.getHits() != null ? article.getHits() + 1 : 1);
                    articleMapper.updateByPrimaryKey(article);
//                    articleMapper.updateById(article);
                }
                return article;
            } else {
                ArticleExample articleExample = new ArticleExample();
                articleExample.createCriteria().andSlugEqualTo(id);
                List<Article> articles = articleMapper.selectByExampleWithBLOBs(articleExample);
//                List<Article> articles = articleMapper.selectById(id);
//                if (articles.size() != 1) {
////                    throw new LingXiException("query content by id and return is not one");
////                }
                if (CollectionUtils.isNotEmpty(articles)) {
                    return articles.get(0);
                }
            }
        }
        return null;
    }

    @Override
    public void updateContentByCid(Article article) {
        if (null != article && null != article.getId()) {
            articleMapper.updateByPrimaryKeySelective(article);
//            articleMapper.updateById(article);
        }
    }

    @Override
    public PageInfo<Article> getArticles(Integer mid, int page, int limit) {
        int total = metaMapper.countWithSql(mid);
        PageHelper.startPage(page, limit);
        List<Article> list = articleMapper.findByCatalog(mid);
        PageInfo<Article> paginator = new PageInfo<>(list);
        paginator.setTotal(total);
        return paginator;
    }

    @Override
    public PageInfo<Article> getArticles(String keyword, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        ArticleExample articleExample = new ArticleExample();
        ArticleExample.Criteria criteria = articleExample.createCriteria();
        criteria.andTypeEqualTo(TypeEnum.ARTICLE.getType());
        criteria.andStatusEqualTo(TypeEnum.PUBLISH.getType());
        criteria.andTitleLike("%" + keyword + "%");
        articleExample.setOrderByClause("created desc");
        List<Article> articles = articleMapper.selectByExampleWithBLOBs(articleExample);
        return new PageInfo<>(articles);
    }

    @Override
    public PageInfo<Article> getArticlesWithpage(ArticleExample commentVoExample, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Article> articles = articleMapper.selectByExampleWithBLOBs(commentVoExample);
        return new PageInfo<>(articles);
    }

    @Override
    @Transactional
    public String deleteByCid(Integer id) {
        Article article = getArticle(id + "");
        if (null != article) {
            articleMapper.deleteByPrimaryKey(id);
            relationshipService.deleteById(id, null);
            return WebConstant.SUCCESS_RESULT;
        }
        return "数据为空";
    }

    @Override
    public void updateCategory(String ordinal, String newCatefory) {
        Article article = new Article();
        article.setCategories(newCatefory);
        ArticleExample example = new ArticleExample();
        example.createCriteria().andCategoriesEqualTo(ordinal);
        articleMapper.updateByExampleSelective(article, example);
    }

    @Override
    public ServiceMultiResult<ArticleDTO> query(BrowseSearch browseSearch) {
        if (StringUtils.isNotNull(browseSearch.getKeywords())) {
            ServiceMultiResult<Integer> serviceResult = searchService.query(browseSearch);
            if (serviceResult.getTotal() == 0) {
                return new ServiceMultiResult<>(0, new ArrayList<>());
            }

            List<ArticleDTO> articleDTOList = Lists.newArrayList();
            serviceResult.getResult().forEach(id -> {
                Article article = articleMapper.selectByPrimaryKey(id);
                articleDTOList.add(modelMapper.map(article, ArticleDTO.class));
            });

            return new ServiceMultiResult<>(serviceResult.getTotal(), articleDTOList);
        } else {
            return simpleQuery(browseSearch);
        }
    }

    private ServiceMultiResult<ArticleDTO> simpleQuery(BrowseSearch browseSearch) {

        int page = browseSearch.getStart() / browseSearch.getSize();
        PageHelper.startPage(page, browseSearch.getSize());

        ArticleExample articleExample = new ArticleExample();
        ArticleExample.Criteria criteria = articleExample.createCriteria();
        criteria.andTypeEqualTo(TypeEnum.ARTICLE.getType());
        criteria.andStatusEqualTo(TypeEnum.PUBLISH.getType());

        if (StringUtils.isNotNull(browseSearch.getCategoryName()) && !"*".equals(browseSearch.getCategoryName())) {
            criteria.andCategoriesEqualTo(browseSearch.getCategoryName());
        }
        RangeValueBlock hitsBlock = RangeValueBlock.matchHits(browseSearch.getHitsBlock());
        if (!RangeValueBlock.ALL.equals(hitsBlock)) {
            if (hitsBlock.getMin() > 0 && hitsBlock.getMax() >=  hitsBlock.getMin()) {
                criteria.andHitsBetween(hitsBlock.getMin(), hitsBlock.getMax());
            }
            if (hitsBlock.getMin() > 0 && hitsBlock.getMax() < 0) {
                criteria.andHitsGreaterThan(hitsBlock.getMin());
            }
            if (hitsBlock.getMax() > 0 && hitsBlock.getMin() < 0) {
                criteria.andHitsLessThan(hitsBlock.getMax());
            }
        }
        RangeValueBlock wordCntBlock = RangeValueBlock.matchWordCnt(browseSearch.getWordCntBlock());
        if (!RangeValueBlock.ALL.equals(wordCntBlock)) {
            if (wordCntBlock.getMin() > 0 && wordCntBlock.getMax() >=  wordCntBlock.getMin()) {
                criteria.andWordCntBetween(wordCntBlock.getMin(), wordCntBlock.getMax());
            }
            if (wordCntBlock.getMin() > 0 && wordCntBlock.getMax() < 0) {
                criteria.andWordCntGreaterThan(wordCntBlock.getMin());
            }
            if (wordCntBlock.getMax() > 0 && wordCntBlock.getMin() < 0) {
                criteria.andWordCntLessThan(wordCntBlock.getMax());
            }
        }

        articleExample.setOrderByClause(browseSearch.getOrderBy() + " " + browseSearch.getOrderDirect());
        List<Article> articles = articleMapper.selectByExampleWithBLOBs(articleExample);

        List<ArticleDTO> articleDTOS = articles.stream().map(article -> modelMapper.map(article, ArticleDTO.class)).collect(Collectors.toList());

        return new ServiceMultiResult<>(articles.size(), articleDTOS);
    }

    @Override
    @Transactional
    public String updateArticle(Article article) {
        if (null == article) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(article.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(article.getContent())) {
            return "文章内容不能为空";
        }
        int titleLength = article.getTitle().length();
        if (titleLength > WebConstant.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        int contentLength = article.getContent().length();
        if (contentLength > WebConstant.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        article.setWordCnt(contentLength);
        if (null == article.getAuthorId()) {
            return "请登录后发布文章";
        }
        if (StringUtils.isBlank(article.getThumbnail())) {
            article.setThumbnail(null);
        }
        int time = DateKit.getCurrentUnixTime();
        article.setModified(time);
        Integer id = article.getId();

        article.setContent(article.getContent());

        articleMapper.updateByPrimaryKey(article);
        relationshipService.deleteById(id, null);
        metasService.saveMetas(id, article.getTags(), TypeEnum.TAG.getType());
        metasService.saveMetas(id, article.getCategories(), TypeEnum.CATEGORY.getType());
        //同步索引数据
        searchService.index(id);

        return WebConstant.SUCCESS_RESULT;
    }

    public ValueOperations valueOperations() {
        return redisTemplate.opsForValue();
    }
}
