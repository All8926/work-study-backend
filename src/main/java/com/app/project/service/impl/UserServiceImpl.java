package com.app.project.service.impl;

import static com.app.project.constant.UserConstant.USER_LOGIN_STATE;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.user.UserRegisterRequest;
import com.app.project.model.dto.user.UserUpdatePasswordRequest;
import com.app.project.model.enums.AddStatusEnum;
import com.app.project.model.enums.UserRoleEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.BusinessException;
import com.app.project.mapper.UserMapper;
import com.app.project.model.dto.user.UserQueryRequest;
import com.app.project.model.entity.User;
import com.app.project.model.vo.UserVO;
import com.app.project.service.UserService;
import com.app.project.utils.SqlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现
 *
 * @author
 * @from
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "work-study";

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequestd) {
        String userAccount = userRegisterRequestd.getUserAccount();
        String userPassword = userRegisterRequestd.getUserPassword();
        String checkPassword = userRegisterRequestd.getCheckPassword();
         String userRole = userRegisterRequestd.getUserRole();

        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 角色是否存在
        List<String> values = UserRoleEnum.getValues();
        if (!values.contains(userRole)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
        }
        // 单机锁，账号不能重复
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            BeanUtils.copyProperties(userRegisterRequestd, user);
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            // 生成随机用户昵称
//            Integer random = RandomUtil.randomInt(999, 9999);
//            String userName = "用户" + random;
//            user.setUserName(userName);

            // 普通用户注册无需审核
            if(userRole.equals(UserRoleEnum.USER.getValue())){
                user.setStatus(AddStatusEnum.RESOLVED.getValue());
            }

            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public String userLogin(String userAccount, String userPassword) {

        // 1. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不存在或密码错误");
        }

        // 2.校验是否在审核中
        if(Objects.equals(user.getStatus(), AddStatusEnum.IN_REVIEW.getValue())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "正在审核中，请耐心等待");
        }
        // 审核不通过
        if(Objects.equals(user.getStatus(), AddStatusEnum.REJECTED.getValue())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册审核不通过，请联系管理员");
        }

        // 3. 记录用户的登录态
        StpUtil.login(user.getId());

        // 将用户信息存入 Sa-Session
        StpUtil.getSession().set(USER_LOGIN_STATE, user);
        // 返回token
        return StpUtil.getTokenValue();
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    @Override
    public User getLoginUser() {
        // 先判断是否已登录
        Object userObj = StpUtil.getSession().get(USER_LOGIN_STATE);
        User currentUser = ((JSONObject) userObj).toJavaObject(User.class);
        // 判断是否为空
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User user = this.getById(currentUser.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        return user;
    }


    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String nickName = userQueryRequest.getNickName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        String userPhone = userQueryRequest.getUserPhone();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.eq(StringUtils.isNotBlank(userPhone), "userPhone", userPhone);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(nickName), "nickName", nickName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(UserUpdatePasswordRequest userUpdateRequest ) {
        long userId = userUpdateRequest.getId();
        User user = this.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        User loginUser = this.getLoginUser();
        long loginUserId = loginUser.getId();
        ThrowUtils.throwIf(userId != loginUserId, ErrorCode.NO_AUTH_ERROR, "无权限");

        String oldPassword = userUpdateRequest.getOldPassword();
        String newPassword = userUpdateRequest.getNewPassword();
        String checkPassword = userUpdateRequest.getCheckPassword();

        // 1.校验密码格式
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        if (newPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 2.旧密码加盐后与数据库密码进行匹配
        String encryptOldPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        if (!encryptOldPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
        }
        // 3.设置新密码
        String encryptNewPassword = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());
        user.setUserPassword(encryptNewPassword);
        boolean result = this.updateById(user);
        // 4.移除登录态
        if (result) {
            StpUtil.logout(loginUserId);
        }

        return result;
    }
}
