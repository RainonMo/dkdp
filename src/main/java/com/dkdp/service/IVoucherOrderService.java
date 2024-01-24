package com.dkdp.service;

import com.dkdp.dto.Result;
import com.dkdp.entity.VoucherOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  订单服务
 * </p>
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 创建订单
     */
    Result createVoucherOrder(Long voucherId);
}
