package cn.edu.sdu.wh.lqy.lingxi.blog.model.browse;

import java.io.Serializable;

public class BrowseSearch implements Serializable {

    private String categoryName;

    private String hitsBlock;

    private String wordCntBlock;

    private String keywords;

    private String orderBy = "created";

    private String orderDirect = "desc";

    private int start = 0;

    private int size = 10;

    public int getStart() {
        return start > 0 ? start : 0;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        if (this.size < 1) {
            return 10;
        } else if (this.size > 100) {
            return 100;
        } else {
            return this.size;
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getHitsBlock() {
        return hitsBlock;
    }

    public void setHitsBlock(String hitsBlock) {
        this.hitsBlock = hitsBlock;
    }

    public String getWordCntBlock() {
        return wordCntBlock;
    }

    public void setWordCntBlock(String wordCntBlock) {
        this.wordCntBlock = wordCntBlock;
    }

    public String getOrderDirect() {
        return orderDirect;
    }

    public void setOrderDirect(String orderDirect) {
        this.orderDirect = orderDirect;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }


    @Override
    public String toString() {
        return "BrowseSearch{" +
                "categoryName='" + categoryName + '\'' +
                ", hitsBlock='" + hitsBlock + '\'' +
                ", wordCntBlock='" + wordCntBlock + '\'' +
                ", keywords='" + keywords + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", orderDirect='" + orderDirect + '\'' +
                ", start=" + start +
                ", size=" + size +
                '}';
    }
}
