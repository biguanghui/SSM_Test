package com.bdqn.ssm.service;

import com.bdqn.ssm.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {
    public User getLoginUser(String userCode, String userPassword);
    /**
     * 通过用户名以及角色进行模糊查询、分页查询用户信息
     * @param userName
     * @param userRole
     * @param currentPageNo
     * @param pageSize
     * @return  int
     */
    public List<User> getUserList(String userName,
                                  Integer userRole,
                                  Integer currentPageNo,
                                  Integer pageSize);
    /**
     * 统计用户总记录数
     * @param userName 用户名
     * @param userRole 角色ID
     * @return int
     *
     */
    public int getUserCount(String userName,Integer userRole);

    /**
     * 添加用户信息
     * @param user
     * @return
     */
    public int add(User user);

    /**
     * 通过用户编码查看用户信息是否存在
     * @param userCode
     * @return
     */
    public User getUserExist(String userCode);

    /**
     * 通过id查询用户信息
     * @param userId
     * @return
     */
    public  User getUserById(String userId);

    public int updateUser(User user);
}
