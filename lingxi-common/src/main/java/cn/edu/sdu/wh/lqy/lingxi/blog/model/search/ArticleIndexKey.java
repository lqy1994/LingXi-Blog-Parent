package cn.edu.sdu.wh.lqy.lingxi.blog.model.search;

import java.io.Serializable;

/**
 * ES索引关键词统一定义
 */
public class ArticleIndexKey implements Serializable {

    public static final String ARTICLE_ID = "id";
    public static final String TITLE = "title";
    public static final String THUMBNAIL = "thumbnail";
    public static final String CREATED = "created";
    public static final String MODIFIED = "modified";
    public static final String AUTHOR_ID = "authorId";
    public static final String TYPE = "type";
    public static final String STATUS = "status";
    public static final String TAGS = "tags";
    public static final String CATEGORIES = "categories";
    public static final String HITS = "hits";
    public static final String COMMENTS_NUM = "commentsNum";
    public static final String ALLOW_COMMENT = "allowComment";
    public static final String ALLOW_PING = "allowPing";
    public static final String CONTENT = "content";
    public static final String WORD_CNT = "wordCnt";


    public static final String AGG_META = "agg_meta"; //标签聚合
    public static final String AGG_COMMENTS = "agg_comments";//评论聚合

}

