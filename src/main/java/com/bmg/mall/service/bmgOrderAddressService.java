package com.bmg.mall.service;

import com.bmg.mall.controller.vo.bmgOrderDetailVO;
import com.bmg.mall.controller.vo.bmgOrderItemVO;
import com.bmg.mall.controller.vo.bmgShoppingCartItemVO;
import com.bmg.mall.controller.vo.bmgUserVO;
import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.entity.bmgOrderItem;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;

import java.util.List;

public interface bmgOrderAddressService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getbmgOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param bmgOrder
     * @return
     */
    String updateOrderInfo(bmgOrder bmgOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(bmgUserVO user, List<bmgShoppingCartItemVO> myShoppingCartItems);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    bmgOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    bmgOrder getbmgOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);


    /**
     * 添加收货信息
     *
     * @param user
     * @param orderNo
     * @return
     */
    String insertAddress(bmgUserVO user, String orderNo,String address,String kuaidi);

}
