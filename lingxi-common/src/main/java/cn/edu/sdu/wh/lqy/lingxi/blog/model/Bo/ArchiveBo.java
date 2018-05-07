package cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;

import java.io.Serializable;
import java.util.List;

/**
 */
public class ArchiveBo implements Serializable {

    private String date;
    private String count;
    private List<Article> articles;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "Archive [" +
                "date='" + date + '\'' +
                ", count='" + count + '\'' +
                ", articles=" + articles +
                ']';
    }
}
