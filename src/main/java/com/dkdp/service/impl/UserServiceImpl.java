package com.dkdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dkdp.dto.LoginFormDTO;
import com.dkdp.dto.Result;
import com.dkdp.dto.UserDTO;
import com.dkdp.entity.User;
import com.dkdp.mapper.UserMapper;
import com.dkdp.service.IUserService;
import com.dkdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.dkdp.utils.RedisConstants.*;

/**
 * <p>
 * 用户服务实现
 * </p>
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1. 校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号有误");
        }
        //2. 生成验证码
        String code = RandomUtil.randomNumbers(6);
        //3. 保存到session/redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+phone,"code",LOGIN_CODE_TTL, TimeUnit.MINUTES);
        log.debug("发送短信验证码成功,验证码为:{}","code");
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //1. 校验手机号
        if(RegexUtils.isPhoneInvalid(loginForm.getPhone())){
            return Result.fail("手机号有误");
        }
        //2. 校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + loginForm.getPhone());
        if(cacheCode==null||!cacheCode.equals(loginForm.getCode())){
            return Result.fail("验证码有误");
        }
        //3. 根据手机号查询用户
        User user = query().eq("phone", loginForm.getPhone()).one();
        //4. 用户信息不存在，则创建新用户
        if(user==null){
            user = createUserWithPhone(loginForm.getPhone());
        }
        //5. 保存用户信息到session/redis
        String token = UUID.randomUUID().toString(true);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        stringRedisTemplate.expire(tokenKey,LOGIN_USER_TTL,TimeUnit.MINUTES);
        return Result.ok(token);
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName("user_" + RandomUtil.randomString(10));
        save(user);
        return user;
    }
}
