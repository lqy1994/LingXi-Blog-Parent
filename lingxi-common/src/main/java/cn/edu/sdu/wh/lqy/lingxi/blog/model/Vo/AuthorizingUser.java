package cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo;

import java.io.Serializable;

public class AuthorizingUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 昵称
     */
    private String userName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 加密密码的盐
     */
    private String salt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * 证书凭证
     */
    public String getCredentialsSalt() {
        if (userName != null && salt != null) {
            return userName + salt;
        }
        return null;
    }
}