# 数据库初始化
# @author
# @from

-- 创建库
create database if not exists work_study;

-- 切换库
use work_study;

-- 用户表
create table if not exists `user`
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/enterprise',
    userName     varchar(256)                           null comment '用户姓名',
    nickName     varchar(256)                           null comment '用户昵称',
    userPhone    varchar(256)                           null comment '用户手机号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    status       tinyint      default 0                 not null comment '0-待审核 1-已通过 2-已拒绝',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 公告表
use work_study;
create table if not exists notice
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(128)                       not null comment '标题',
    publishTime datetime                           null comment '发布时间',
    content     text                               null comment '内容',
    status      tinyint  default 0                 not null comment '0-未发布  1-已发布',
    userId      bigint                             not null comment '创建人',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment '公告' collate = utf8mb4_unicode_ci;


-- 意见反馈表
use work_study;
create table if not exists feedback
(
    id               bigint auto_increment comment 'id' primary key,
    title            varchar(128)                       not null comment '标题',
    content          text                               null comment '内容',
    image            varchar(1024)                      null comment '图片',
    userId           bigint                             not null comment '创建人',
    status           tinyint  default 0                 not null comment '0-待处理  1-已处理 2-不予处理',
    responseText     varchar(512)                       null comment '回复内容',
    responseUserId   bigint                             null comment '回复人id',
    responseUserName varchar(128)                       null comment '回复人姓名',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint  default 0                 not null comment '是否删除'
) comment '意见反馈' collate = utf8mb4_unicode_ci;

-- 岗位信息表
use work_study;
create table if not exists job_post
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '创建人Id',
    title          varchar(256)                       not null comment '岗位名称',
    description    varchar(1024)                      null comment '岗位描述',
    salary         varchar(128)                       not null comment '工资',
    requirement    varchar(512)                       null comment '任职要求',
    workAddress    varchar(256)                       null comment '工作地点',
    maxCount       int                                null comment '招聘人数',
    expirationTime datetime                           null comment '截止时间',
    status         tinyint  default 0                 not null comment '0-待审核 1-已发布 2-已下线 3-审核不通过',
    rejectReason   varchar(256)                       null comment '拒绝原因',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除'
) comment '岗位信息' collate = utf8mb4_unicode_ci;

-- 岗位申请表
use work_study;
create table if not exists job_application
(
    id            bigint auto_increment comment 'id' primary key,
    jobId         bigint                             not null comment '岗位ID',
    userId        bigint                             not null comment '用户ID',
    enterpriseId  bigint                             not null comment '企业ID',
    interviewTime datetime                           null comment '面试时间',
    status        tinyint  default 0                 not null comment '0-待审核 1-审核拒绝 2-待面试 3-面试不通过 4-面试通过',
    fileList      varchar(1024)                      null comment '附件列表',
    remark        varchar(256)                       null comment '备注信息',
    auditExplain  varchar(256)                       null comment '审核说明',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
) comment '岗位申请' collate = utf8mb4_unicode_ci;

-- 录用记录表
use work_study;
create table if not exists hiring_record
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null comment '用户Id',
    enterPriseId bigint                             not null comment '企业Id',
    jobPostId    bigint                             not null comment '岗位Id',
    hireDate     datetime                           not null comment '入职日期',
    leaveDate    datetime                           null comment '离职日期',
    fileList     varchar(1024)                      null comment '附件地址',
    status       tinyint  default 0 comment '0-在职 1-离职',
    remark       varchar(256)                       null comment '备注',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
) comment '录用记录' collate = utf8mb4_unicode_ci;

-- 薪酬表
create table if not exists salary
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null comment '用户Id',
    enterPriseId bigint                             not null comment '企业Id',
    salaryType   varchar(32)                        not null comment '0-日结 1-周结 2-月结',
    periodDate   varchar(32)                        not null comment '结算周期',
    workDuration varchar(32)                        null comment '出勤时长',
    performance  varchar(32)                        null comment '绩效等级：A/B/C等',
    salaryAmount varchar(32)                        not null comment '实发工资',
    issueStatus  tinyint  default 0                 not null comment '0-未发 1-已发',
    issueTime    datetime                           null comment '发放时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
) comment '薪酬' collate = utf8mb4_unicode_ci;

-- 考勤表
create table if not exists attendance
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '用户Id',
    enterPriseId   bigint                             not null comment '企业Id',
    attendanceDate date                               not null comment '考勤日期',
    checkInTime    varchar(32)                        null COMMENT '上班打卡时间',
    checkOutTime   varchar(32)                        null COMMENT '下班打卡时间',
    workDuration   varchar(32)                        null comment '工作时长',
    status         tinyint  default 0                 not null comment '0-正常 1-迟到 2-早退 3-旷工 4-请假',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除'
) comment '考勤' collate = utf8mb4_unicode_ci;



