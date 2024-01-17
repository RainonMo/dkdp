package com.dkdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dkdp.entity.User;
import com.dkdp.mapper.UserMapper;
import com.dkdp.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
