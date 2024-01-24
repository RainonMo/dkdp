package com.dkdp.service;

import com.dkdp.dto.Result;
import com.dkdp.entity.SeckillVoucher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-01-04
 */
public interface ISeckillVoucherService extends IService<SeckillVoucher> {

    /**
     * 秒杀
     * @param voucherId
     * @return
     */
    Result seckill(Long voucherId);
}
