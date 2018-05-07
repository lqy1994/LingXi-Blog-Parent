package cn.edu.sdu.wh.lqy.lingxi.blog.exception;

public class LingXiException extends RuntimeException {

    public LingXiException() {
    }

    public LingXiException(String message) {
        super(message);
    }

    public LingXiException(String message, Throwable cause) {
        super(message, cause);
    }

    public LingXiException(Throwable cause) {
        super(cause);
    }

}
