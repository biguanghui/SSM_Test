package com.bdqn.ssm.dao;

import com.bdqn.ssm.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
public User getLoginUser(@Param("userCode") String userCode);
    /**
     * 通过用户名以及角色进行模糊查询、分页查询用户信息
     * @param userName
     * @param userRole
     * @param currentPageNo
     * @param pageSize
     * @return  int
     */
    public List<User> getUserList(@Param("userName") String userName,
                                  @Param("userRole") Integer userRole,
                                  @Param("pageIndex") Integer currentPageNo,
                                  @Param("pageSize") Integer pageSize);
    /**
     * 统计用户总记录数
     * @param userName 用户名
     * @param userRole 角色ID
     * @return int
     *
     */
    public int getUserCount(@Param("userName") String userName,@Param("userRole")
            Integer userRole);

    /**
     * 添加用户信息
     * @param user
     * @return
     */
    public int add(User user);

    /**
     * 通过id查询用户信息
     * @param userId
     * @return
     */
    public User getUserById(@Param("userId") String userId);

    public int updateUser(@Param("user")User user);

}
