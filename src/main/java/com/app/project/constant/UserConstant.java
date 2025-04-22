package com.app.project.constant;

/**
 * 用户常量
 *
 * @author 
 * @from 
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login_id";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    /**
     * 维修员角色
     */
    String ENTERPRISE_ROLE = "enterprise";

    // endregion
}
