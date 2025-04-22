package com.app.project.service;

import com.app.project.model.dto.user.UserRegisterRequest;
import com.app.project.model.dto.user.UserUpdatePasswordRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.app.project.model.dto.user.UserQueryRequest;
import com.app.project.model.entity.User;
import com.app.project.model.vo.UserVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author
 * @from
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册信息
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    String userLogin(String userAccount, String userPassword );


    /**
     * 获取当前登录用户
     *
     * @return
     */
    User getLoginUser( );



    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 更新密码
     * @param userUpdateRequest
     * @return
     */
    boolean updatePassword(UserUpdatePasswordRequest userUpdateRequest );
}
