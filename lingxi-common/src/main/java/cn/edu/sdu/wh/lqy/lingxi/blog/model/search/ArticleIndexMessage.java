package cn.edu.sdu.wh.lqy.lingxi.blog.model.search;

/**
 * 文章内容Kafka消息类
 */
public class ArticleIndexMessage {

    public static final String INDEX = "Article_Index";

    public static final String REMOVE = "Article_Remove";

    public static final int MAX_RETRY = 3;

    private Integer artId;
    //操作种类
    private String operation;
    //重试次数
    private int retry = 0;

    public ArticleIndexMessage() {
    }

    public ArticleIndexMessage(Integer artId, String operation, int retry) {
        this.artId = artId;
        this.operation = operation;
        this.retry = retry;
    }

    public Integer getArtId() {
        return artId;
    }

    public void setArtId(Integer artId) {
        this.artId = artId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    @Override
    public String toString() {
        return "ArticleIndexMessage{" +
                "artId=" + artId +
                ", operation='" + operation + '\'' +
                ", retry=" + retry +
                '}';
    }
}
