package cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Comment;

import java.util.List;

/**
 * 返回页面的评论，包含父子评论内容
 */
public class CommentBo extends Comment {

    private int levels;
    private List<Comment> children;

    public CommentBo(Comment comments) {
        setAuthor(comments.getAuthor());
        setMail(comments.getMail());
        setCoid(comments.getCoid());
        setAuthorId(comments.getAuthorId());
        setUrl(comments.getUrl());
        setCreated(comments.getCreated());
        setAgent(comments.getAgent());
        setIp(comments.getIp());
        setContent(comments.getContent());
        setOwnerId(comments.getOwnerId());
        setArtId(comments.getArtId());
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }
}
