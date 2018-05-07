package cn.edu.sdu.wh.lqy.lingxi.blog.model.search;

import java.io.Serializable;
import java.util.List;

/**
 * 通用多结果Service返回结构
 */
public class ServiceMultiResult<T> implements Serializable {

    private long total;
    private List<T> result;

    public ServiceMultiResult(long total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getResultSize() {
        if (this.result == null) {
            return 0;
        }
        return this.result.size();
    }

    @Override
    public String toString() {
        return "ServiceMultiResult{" +
                "total=" + total +
                ", result=" + result +
                '}';
    }
}
