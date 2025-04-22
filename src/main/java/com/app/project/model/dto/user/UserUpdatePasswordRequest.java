package com.app.project.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户更新密码请求
 *
 * @author 
 * @from 
 */
@Data
public class UserUpdatePasswordRequest implements Serializable {
    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String checkPassword;

    private static final long serialVersionUID = 1L;
}