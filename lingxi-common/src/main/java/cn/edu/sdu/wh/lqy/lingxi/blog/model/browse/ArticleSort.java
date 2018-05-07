package cn.edu.sdu.wh.lqy.lingxi.blog.model.browse;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

public class ArticleSort implements Serializable {

    public static final String DEFAULT_SORT_KEY = "created";

    private static final Set<String> SORT_KEYS = Sets.newHashSet(
        DEFAULT_SORT_KEY,
            "created",
            "modified",
            "hits",
            "word_cnt"
    );

    public static String getSortKey(String key) {
        if (!SORT_KEYS.contains(key)) {
            key = DEFAULT_SORT_KEY;
        }

        return key;
    }
}