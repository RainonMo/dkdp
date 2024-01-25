package com.dkdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dkdp.dto.LoginFormDTO;
import com.dkdp.dto.Result;
import com.dkdp.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  用户服务
 * </p>
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);
}
