package com.bdqn.ssm.service.impl;

import com.bdqn.ssm.dao.RoleDao;
import com.bdqn.ssm.entity.Role;
import com.bdqn.ssm.service.RoleService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleDao rd;
    @Override
    public List<Role> getRoleList() {
        return rd.getRoleList();
    }
}
