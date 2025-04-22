package com.app.project.model.dto.user;

import java.io.Serializable;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户注册请求体
 *
 * @author 
 * @from 
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @NotBlank(message = "账号不能为空")
    @Size(max = 10, message = "账号不能超过10个字符")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    @Size(max = 12,min = 4, message = "密码需要4~12位")
    private String userPassword;

    @NotBlank(message = "确认密码不能为空")
    private String checkPassword;

    @Size(max = 10, message = "不能超过10个字符")
    @NotBlank(message = "姓名不能为空")
    private String userName;


    /**
     * 用户昵称
     */
    @Size(max = 10, message = "昵称不能超过10个字符")
    @NotBlank(message = "昵称不能为空")
    private String nickName;

    /**
     * 用户角色
     */
    @NotBlank(message = "角色不能为空")
    private String userRole;

    @Size(max = 11, message = "手机号不正确")
    @NotBlank(message = "手机号不能为空")
    private String userPhone;
}
