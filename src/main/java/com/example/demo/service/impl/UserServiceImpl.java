package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 继承 ServiceImpl 会自动实现 IService 的所有方法
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}