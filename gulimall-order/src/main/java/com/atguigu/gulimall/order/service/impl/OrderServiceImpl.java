package com.atguigu.gulimall.order.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        return null;
    }

    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    //构建OrderConfirmVo
    //获取当前用户登录的信息
    //TODO :获取当前线程请求头信息(解决Feign异步调用丢失请求头问题)
    //开启第一个异步任务
    //每一个线程都来共享之前的请求数据
    //1、远程查询所有的收获地址列表
    //开启第二个异步任务
    //每一个线程都来共享之前的请求数据
    //2、远程查询购物车所有选中的购物项
    //feign在远程调用之前要构造请求，调用很多的拦截器
    //获取全部商品的id
    //远程查询商品库存信息
    //将skuStockVos集合转换为map
    //3、查询用户积分
    //4、价格数据自动计算
    //TODO 5、防重令牌(防止表单重复提交)
    //为用户设置一个token，三十分钟过期时间（存在redis）
    /**
     * 提交订单
     * @param vo
     * @return
     */
    // @Transactional(isolation = Isolation.READ_COMMITTED) 设置事务的隔离级别
    // @Transactional(propagation = Propagation.REQUIRED)   设置事务的传播级别
    // @GlobalTransactional(rollbackFor = Exception.class)
    //去创建、下订单、验令牌、验价格、锁定库存...
    //获取当前用户登录的信息
    //1、验证令牌是否合法【令牌的对比和删除必须保证原子性】
    //通过lure脚本原子验证令牌和删除令牌
    //令牌验证失败
    //令牌验证成功
    //1、创建订单、订单项等信息
    //2、验证价格
    //金额对比
    //TODO 3、保存订单
    //4、库存锁定,只要有异常，回滚订单数据
    //订单号、所有订单项信息(skuId,skuNum,skuName)
    //获取出要锁定的商品数据信息
    //TODO 调用远程锁定库存的方法
    //出现的问题：扣减库存成功了，但是由于网络原因超时，出现异常，导致订单事务回滚，库存事务不回滚(解决方案：seata)
    //为了保证高并发，不推荐使用seata，因为是加锁，并行化，提升不了效率,可以发消息给库存服务
    //锁定成功
    // int i = 10/0;
    //TODO 订单创建成功，发送消息给MQ
    //删除购物车里的数据
    //锁定失败
    // responseVo.setCode(3);
    // return responseVo;
    /**
     * 按照订单号获取订单信息
     * @param orderSn
     * @return
     */
    /**
     * 关闭订单
     * @param orderEntity
     */
    //关闭订单之前先查询一下数据库，判断此订单状态是否已支付
    //代付款状态进行关单
    // 发送消息给MQ
    //TODO 确保每个消息发送成功，给每个消息做好日志记录，(给数据库保存每一个详细信息)保存每个消息的详细信息
    //TODO 定期扫描数据库，重新发送失败的消息
    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    //保留两位小数点，向上取值
    //查询订单项的数据
    /**
     * 查询当前用户所有订单数据
     * @param params
     * @return
     */
    //遍历所有订单集合
    //根据订单号查询订单项里的数据
    /**
     * 保存订单所有数据
     * @param orderCreateTo
     */
    //获取订单信息
    //保存订单
    //获取订单项信息
    //批量保存订单项数据
    //1、生成订单号
    //2、获取到所有的订单项
    //3、验价(计算价格、积分等信息)
    /**
     * 计算价格的方法
     * @param orderEntity
     * @param orderItemEntities
     */
    //总价
    //优惠价
    //积分、成长值
    //订单总额，叠加每一个订单项的总额信息
    //优惠价格信息
    //总价
    //积分信息和成长值信息
    //1、订单价格相关的
    //设置应付总额(总额+运费)
    //设置积分成长值信息
    //设置删除状态(0-未删除，1-已删除)
    /**
     * 构建订单数据
     * @param orderSn
     * @return
     */
    //获取当前用户登录信息
    //远程获取收货地址和运费信息
    //获取到运费信息
    //获取到收货地址信息
    //设置收货人信息
    //设置订单相关的状态信息
    /**
     * 构建所有订单项数据
     * @return
     */
    //最后确定每个购物项的价格
    //构建订单项数据
    /**
     * 构建某一个订单项的数据
     * @param items
     * @return
     */
    //1、商品的spu信息
    //获取spu的信息
    //2、商品的sku信息
    //使用StringUtils.collectionToDelimitedString将list集合转换为String
    //3、商品的优惠信息
    //4、商品的积分信息
    //5、订单项的价格信息
    //当前订单项的实际金额.总额 - 各种优惠价格
    //原来的价格
    //原价减去优惠价得到最终的价格
    /**
     * 处理支付宝的支付结果
     * @param asyncVo
     * @return
     */
    //保存交易流水信息
    //添加到数据库中
    //修改订单状态
    //获取当前状态
    //支付成功状态
    //获取订单号
    /**
     * 修改订单状态
     * @param orderSn
     * @param code
     */
    /**
     * 微信异步通知结果
     * @param notifyData
     * @return
     */
    //签名效验
    //2.金额效验（从数据库查订单）
    //如果查询出来的数据是null的话
    //比较严重(正常情况下是不会发生的)发出告警：钉钉、短信
    //TODO 发出告警，钉钉，短信
    //判断订单状态状态是否为已支付或者是已取消,如果不是订单状态不是已支付状态
        /*//判断金额是否一致,Double类型比较大小，精度问题不好控制
        if (orderEntity.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
            //TODO 告警
            throw new RuntimeException("异步通知中的金额和数据库里的不一致,orderNo=" + payResponse.getOrderId());
        }*/
    //3.修改订单支付状态
    //支付成功状态
    //4.告诉微信不要再重复通知了
    /**
     * 创建秒杀单
     * @param orderTo
     */
    //TODO 保存订单信息
    //保存订单
    //保存订单项信息
    //保存商品的spu信息

    //保存订单项数据
}