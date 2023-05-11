package com.atguigu.gulimall.ware.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.vo.SkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.OrderItemVo;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            Long count = baseMapper.getSkuStock(skuId);
            skuHasStockVo.setHasStock(count != null && count > 0);
            skuHasStockVo.setSkuId(skuId);

            return skuHasStockVo;
        }).collect(Collectors.toList());
    }


    /**
     * 为某个订单锁定库存
     *
     * @param vo
     * @return
     */
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单详情信息
         * 追溯
         */
        //1、按照下单的收货地址，找到一个就近仓库，锁定库存
        //2、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHashStock> collect = locks.stream().map(item -> {
            SkuWareHashStock stock = new SkuWareHashStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪个仓库有库存
            List<Long> wareIdList = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIdList);
            return stock;
        }).collect(Collectors.toList());
        //2、锁定库存
        for (SkuWareHashStock hasStock : collect) {
            boolean skuStock = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (CollUtil.isEmpty(wareIds)) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //锁定成功就返回1，失败就返回0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (1 == count) {
                    skuStock = true;
                    break;
                } else {
                    //当前仓库锁失败，重试下一个仓库
                }
            }
            if (!skuStock) {
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
            //1、如果每一个商品都锁定成功,将当前商品锁定了几件的工作单记录发给MQ
            //2、锁定失败。前面保存的工作单信息都回滚了。发送出去的消息，即使要解锁库存，由于在数据库查不到指定的id，所有就不用解锁
            //TODO 告诉MQ库存锁定成功
        }
        //3、肯定全部都是锁定成功的
        return true;
    }

    @Data
    private class SkuWareHashStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;

    }
    //库存工作单的id
    /**
     * 解锁
     * 1、查询数据库关于这个订单锁定库存信息
     *   有：证明库存锁定成功了
     *      解锁：订单状况
     *          1、没有这个订单，必须解锁库存
     *          2、有这个订单，不一定解锁库存
     *              订单状态：已取消：解锁库存
     *                      已支付：不能解锁库存
     */
    //查出wms_ware_order_task工作单的信息
    //获取订单号查询订单状态
    //远程查询订单信息
    //订单数据返回成功
    //判断订单状态是否已取消或者支付或者订单不存在
    //订单已被取消，才能解锁库存
    //当前库存工作单详情状态1，已锁定，但是未解锁才可以解锁
    //消息拒绝以后重新放在队列里面，让别人继续消费解锁
    //远程调用服务失败
    //无需解锁
    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存优先到期，查订单状态新建，什么都不处理
     * 导致卡顿的订单，永远都不能解锁库存
     * @param orderTo
     */
    //查一下最新的库存解锁状态，防止重复解锁库存
    //按照工作单的id找到所有 没有解锁的库存，进行解锁
    /**
     * 解锁库存的方法
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    //库存解锁
    //更新工作单的状态
    //变为已解锁
}