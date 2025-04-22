# 勤工俭学后台管理系统 - 后端
### 技术实现
- Java SpringBoot2 框架
- MySQL数据库 + Mybatis-Plus 框架 + Mybatis X
- Redis缓存 
- Sa-Token 权限控制
- COS 对象存储
- knife4j 接口文档
### 开发环境
- IDEA
- JDK 1.8
- MySQL 5.7
- Redis 5.0.14
### 运行步骤
- 打开 application.yml 文件，修改数据库连接信息、redis连接信息、腾讯云对象存储信息
- 执行数据库脚本 /sql/create_table.sql ，其余 sql 文件为对应表的数据
- 运行项目，访问接口地址： http://localhost:8800/doc.html
- 登录账号：admin，密码：123456
### 目前进度
- 登录注册
- 用户管理
- 公告管理