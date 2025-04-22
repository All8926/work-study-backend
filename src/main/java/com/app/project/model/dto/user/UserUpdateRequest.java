package com.app.project.model.dto.user;

import java.io.Serializable;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户更新请求
 *
 * @author 
 * @from 
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    @NotNull
    private Long id;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 用户姓名
     */
    @Size(max = 10, message = "不能超过10个字符")
    private String userName;

    /**
     * 用户昵称
     */
    @Size(max = 20, message = "不能超过20个字符")
    private String nickName;

    /**
     * 用户手机号
     */
    @Size(max = 11, message = "不能超过10个字符")
    private String userPhone;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    @Size(max = 500, message = "不能超过500个字符")
    private String userProfile;

    /**
     * 账号状态：0-审核中 1-通过 2-不通过
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}