package com.app.project.security;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.app.project.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.app.project.constant.UserConstant.USER_LOGIN_STATE;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return org.elasticsearch.core.List.of(); // 暂不启用权限码
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Object userObj = StpUtil.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);

        // 手动转实体
        User user = userObj instanceof JSONObject
                ? ((JSONObject) userObj).toJavaObject(User.class)
                : (User) userObj;

        return user != null
                ? org.elasticsearch.core.List.of(user.getUserRole())
                : org.elasticsearch.core.List.of();
    }
}
