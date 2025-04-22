package com.app.project.model.dto.user;

import java.io.Serializable;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户创建请求
 *
 * @author 
 * @from 
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    @Size(max = 10, message = "不能超过10个字符")
    private String userName;

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(max = 10, message = "不能超过10个字符")
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}