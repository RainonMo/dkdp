package com.dkdp.service.impl;

import com.dkdp.dto.Result;
import com.dkdp.entity.VoucherOrder;
import com.dkdp.mapper.VoucherOrderMapper;
import com.dkdp.service.ISeckillVoucherService;
import com.dkdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dkdp.utils.RedisIdWorker;
import com.dkdp.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  订单服务
 * </p>
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;

    @Override
    @Transactional
    public Result createVoucherOrder(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        Integer count = seckillVoucherService.query().eq("voucher_id", voucherId).eq("user_id", userId).count();
        if(count>0){
            return Result.fail("不能重复下单");
        }
        //3. 扣减库存
        boolean isSuccess = seckillVoucherService.update().setSql("stock=stock-1").eq("voucher_id", voucherId).gt("stock", 0).update();
        if(!isSuccess){
            return Result.fail("库存不足");
        }
        //4. 添加订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 全局唯一id
        long id = redisIdWorker.nextId("order");
        voucherOrder.setId(id);
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setUserId(userId);
        return Result.ok(id);
    }
}
