package cn.edu.sdu.wh.lqy.lingxi.blog.service.impl;

import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.ArticleMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.ArticleSearch;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.ArticleSort;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.BrowseSearch;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.RangeValueBlock;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.search.*;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ISearchService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Service(interfaceClass = ISearchService.class)
public class SearchServiceImpl implements ISearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ISearchService.class);

    private static final String INDEX_NAME = "lingxi_sug";

    private static final String INDEX_TYPE = "article";

    private static final String INDEX_TOPIC = "lingxi_article_topic";

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TransportClient elasticsearchClient;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Gson gson;

    /**
     * kafka消息处理
     *
     * @param msg
     */
    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String msg) {
        try {
            ArticleIndexMessage message = objectMapper.readValue(msg, ArticleIndexMessage.class);

            switch (message.getOperation()) {
                case ArticleIndexMessage.INDEX:
                    this.createOrUpdateIndex(message);
                    break;
                case ArticleIndexMessage.REMOVE:
                    this.removeIndex(message);
                    break;
                default:
                    LOGGER.warn("Not support message content " + msg);
                    break;
            }
        } catch (IOException e) {
            LOGGER.error("Cannot parse json for " + msg + "Error: ", e);
        }
    }

    private void createOrUpdateIndex(ArticleIndexMessage message) {

        Integer artId = message.getArtId();
        Article article = articleMapper.selectByPrimaryKey(artId);
        if (article == null) {
            LOGGER.error("Index Article:{} dose not exist!", artId);
            index(artId, message.getRetry() + 1);
            return;
        }

        ArticleIndexTemplate indexTemplate = new ArticleIndexTemplate();
        modelMapper.map(article, indexTemplate);

        SearchRequestBuilder requestBuilder = elasticsearchClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(ArticleIndexKey.ARTICLE_ID, artId));
        LOGGER.debug(requestBuilder.toString());

        SearchResponse searchResponse = requestBuilder.get();
        boolean success = false;
        long totalHit = searchResponse.getHits().getTotalHits();
        if (totalHit == 0) {
            success = create(indexTemplate);
        } else if (totalHit == 1) {
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId, indexTemplate);
        } else {
            success = deleteAndCreate(totalHit, indexTemplate);
        }
    }

    private void removeIndex(ArticleIndexMessage message) {

        Integer artId = message.getArtId();
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(elasticsearchClient)
                .filter(QueryBuilders.termQuery(ArticleIndexKey.ARTICLE_ID, artId))
                .source(INDEX_NAME);

        LOGGER.debug("Delete by query for content: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        LOGGER.debug("Delete total " + deleted);
    }


    @Override
    public void index(Integer contentId) {
        this.index(contentId, 0);
    }

    private void index(Integer contentId, int retry) {
        if (retry > ArticleIndexMessage.MAX_RETRY) {
            LOGGER.error("Retry index times over 3 for content: " + contentId + " Please check it!");
            return;
        }

        ArticleIndexMessage message = new ArticleIndexMessage(contentId, ArticleIndexMessage.INDEX, retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOGGER.error("Json encode error for " + message);
        }
    }

    public boolean create(ArticleIndexTemplate indexTemplate) {
        if (!updateSuggest(indexTemplate)) {
            return false;
        }

        try {
            IndexResponse response = elasticsearchClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            LOGGER.debug("Create index with Article: " + indexTemplate.getId());
            if (RestStatus.CREATED == response.status()) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error to index Article " + indexTemplate.getId(), e);
            return false;
        }
    }

    private boolean update(String esId, ArticleIndexTemplate indexTemplate) {
        if (!updateSuggest(indexTemplate)) {
            return false;
        }

        try {
            UpdateResponse response = elasticsearchClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            LOGGER.debug("Update index with Article: " + indexTemplate.getId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error to index Article " + indexTemplate.getId(), e);
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit, ArticleIndexTemplate indexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(elasticsearchClient)
                .filter(QueryBuilders.termQuery(ArticleIndexKey.ARTICLE_ID, indexTemplate.getId()))
                .source(INDEX_NAME);
        LOGGER.debug("Delete by query for article: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            LOGGER.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(indexTemplate);
        }
    }

    @Override
    public void remove(Integer contentId) {
        this.remove(contentId, 0);
    }

    private boolean updateSuggest(ArticleIndexTemplate indexTemplate) {
        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(
                elasticsearchClient, AnalyzeAction.INSTANCE, INDEX_NAME, indexTemplate.getTitle(),
                indexTemplate.getContent()
        );

        requestBuilder.setAnalyzer("ik_max_word");

        AnalyzeResponse response = requestBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        if (tokens == null) {
            LOGGER.warn("Can not analyze token for article: " + indexTemplate.getId());
            return false;
        }

        List<ArticleSuggest> suggests = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // 排序数字类型 & 小于2个字符的分词结果
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }

            ArticleSuggest suggest = new ArticleSuggest();
            suggest.setInput(token.getTerm());
            suggests.add(suggest);
        }

        // 定制化自动补全
        ArticleSuggest suggest = new ArticleSuggest();
        suggest.setInput(indexTemplate.getCategories());
        suggests.add(suggest);

        indexTemplate.setSuggest(suggests);
        return true;
    }

    @Override
    public ServiceMultiResult<Integer> query(ArticleSearch articleSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.must(
                QueryBuilders.multiMatchQuery(articleSearch.getTitle(),
                        ArticleIndexKey.TITLE, ArticleIndexKey.TAGS, ArticleIndexKey.CONTENT
                ));

        SearchRequestBuilder requestBuilder = elasticsearchClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(articleSearch.getOrderBy(),
                        SortOrder.fromString(articleSearch.getOrderDirection()))
                .setFrom(articleSearch.getStart())
                .setSize(articleSearch.getSize())
                .setFetchSource(ArticleIndexKey.ARTICLE_ID, null);

        LOGGER.info("Search Request Builder --- {}", requestBuilder.toString());

        List<Integer> contentIds = new ArrayList<>();
        SearchResponse response = requestBuilder.get();

        if (response.status() != RestStatus.OK) {
            LOGGER.warn("Search status is not ok for " + requestBuilder);
            return new ServiceMultiResult<>(0, contentIds);
        }
        for (SearchHit hit : response.getHits()) {
            System.out.println(hit.getSource());
            contentIds.add(Ints.tryParse(String.valueOf(hit.getSource().get(ArticleIndexKey.ARTICLE_ID))));
        }
        return new ServiceMultiResult<>(response.getHits().totalHits, contentIds);
    }


    @Override
    public ServiceMultiResult<Integer> query(BrowseSearch browseSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //Category
        if (StringUtils.isNotNull(browseSearch.getCategoryName()) && !"*".equals(browseSearch.getCategoryName())) {
            boolQuery.filter(QueryBuilders.termQuery(ArticleIndexKey.CATEGORIES, browseSearch.getCategoryName()));
        }
        //Hits
        RangeValueBlock hitsBlock = RangeValueBlock.matchHits(browseSearch.getHitsBlock());
        if (!RangeValueBlock.ALL.equals(hitsBlock)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(ArticleIndexKey.HITS);
            if (hitsBlock.getMax() > 0) {
                rangeQueryBuilder.lte(hitsBlock.getMax());
            }
            if (hitsBlock.getMin() > 0) {
                rangeQueryBuilder.gte(hitsBlock.getMin());
            }
            boolQuery.filter(rangeQueryBuilder);
        }
        //WordCnt
        RangeValueBlock wordCntBlock = RangeValueBlock.matchWordCnt(browseSearch.getWordCntBlock());
        if (!RangeValueBlock.ALL.equals(wordCntBlock)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(ArticleIndexKey.WORD_CNT);
            if (wordCntBlock.getMax() > 0) {
                rangeQueryBuilder.lte(wordCntBlock.getMax());
            }
            if (wordCntBlock.getMin() > 0) {
                rangeQueryBuilder.gte(wordCntBlock.getMin());
            }
            boolQuery.filter(rangeQueryBuilder);
        }


        boolQuery.must(
                QueryBuilders.multiMatchQuery(browseSearch.getKeywords(),
                        ArticleIndexKey.TITLE,
                        ArticleIndexKey.CONTENT,
                        ArticleIndexKey.CATEGORIES
                ));

        SearchRequestBuilder requestBuilder = elasticsearchClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE).setQuery(boolQuery)
                .addSort(esSortKey(browseSearch),
                        SortOrder.fromString(browseSearch.getOrderDirect()))
                .setFrom(browseSearch.getStart())
                .setSize(browseSearch.getSize())
                .setFetchSource(ArticleIndexKey.ARTICLE_ID, null);

        LOGGER.debug(requestBuilder.toString());

        List<Integer> artIds = new ArrayList<>();
        SearchResponse response = requestBuilder.get();
        if (response.status() != RestStatus.OK) {
            LOGGER.warn("Search status is not ok for " + requestBuilder);
            return new ServiceMultiResult<>(0, artIds);
        }

        for (SearchHit hit : response.getHits()) {
            LOGGER.info(gson.toJson(hit.getSource()));
            artIds.add(Integer.parseInt(String.valueOf(hit.getSource().get(ArticleIndexKey.ARTICLE_ID))));
        }

        return new ServiceMultiResult<>(response.getHits().totalHits, artIds);
    }

    private String esSortKey(BrowseSearch browseSearch) {
        String sortKey = ArticleSort.getSortKey(browseSearch.getOrderBy());
        switch (sortKey) {
            case ArticleIndexKey.HITS:
                return ArticleIndexKey.HITS;
            case "word_cnt":
                return ArticleIndexKey.WORD_CNT;
            default:
                return sortKey;
        }
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix) {

        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest")
                .prefix(prefix).size(5);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("autocomplete", suggestion);

        SearchRequestBuilder requestBuilder = elasticsearchClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .suggest(suggestBuilder);

        LOGGER.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        Suggest suggest = response.getSuggest();
        if (suggest == null) {
            return ServiceResult.of(new ArrayList<>());
        }
        Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

        int maxSuggest = 0;
        Set<String> suggestSet = new HashSet<>();

        for (Object term : result.getEntries()) {
            if (term instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;

                if (item.getOptions().isEmpty()) {
                    continue;
                }

                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    if (suggestSet.contains(tip)) {
                        continue;
                    }
                    suggestSet.add(tip);
                    maxSuggest++;
                }
            }
            if (maxSuggest > 5) {
                break;
            }
        }
        List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
        return ServiceResult.of(suggests);
    }

    private void remove(Integer artId, int retry) {
        if (retry > ArticleIndexMessage.MAX_RETRY) {
            LOGGER.error("Retry remove times over 3 for content: " + artId + " Please check it!");
            return;
        }

        ArticleIndexMessage message = new ArticleIndexMessage(artId, ArticleIndexMessage.REMOVE, retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot encode json for " + message, e);
        }
    }

}
