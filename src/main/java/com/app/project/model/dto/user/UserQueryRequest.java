package com.app.project.model.dto.user;

import com.app.project.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 *
 * @author 
 * @from 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
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
     * 用户简介
     */
    private String userProfile;



    private static final long serialVersionUID = 1L;
}