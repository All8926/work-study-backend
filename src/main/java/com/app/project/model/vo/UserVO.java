package com.app.project.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 用户视图（脱敏）
 *
 * @author 
 * @from 
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 账号状态：0-审核中 1-通过 2-不通过
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}