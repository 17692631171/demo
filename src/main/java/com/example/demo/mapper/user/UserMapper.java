package com.example.demo.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email} AND deleted = 0")
    User selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND deleted = 0")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据状态查询用户列表
     */
    @Select("SELECT * FROM user WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<User> selectByStatus(@Param("status") Integer status);

    /**
     * 查询所有启用用户
     */
    @Select("SELECT * FROM user WHERE status = 1 AND deleted = 0")
    List<User> selectEnabledUsers();

    /**
     * 批量更新用户状态
     */
    int updateStatusBatch(@Param("userIds") List<Long> userIds, @Param("status") Integer status);

    /**
     * 更新最后登录时间
     */
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 根据条件分页查询用户
     */
    List<User> selectUserPage(@Param("username") String username,
                              @Param("phone") String phone,
                              @Param("status") Integer status,
                              @Param("startTime") String startTime,
                              @Param("endTime") String endTime);

    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0")
    Long countUsers();

    /**
     * 统计今日新增用户
     */
    @Select("SELECT COUNT(*) FROM user WHERE DATE(create_time) = CURDATE() AND deleted = 0")
    Long countTodayNewUsers();
}