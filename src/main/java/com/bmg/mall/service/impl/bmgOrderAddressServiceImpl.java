package com.bmg.mall.service.impl;

import com.bmg.mall.common.*;
import com.bmg.mall.controller.vo.*;
import com.bmg.mall.dao.*;
import com.bmg.mall.entity.StockNumDTO;
import com.bmg.mall.entity.bmgGoods;
import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderItem;
import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.service.bmgOrderAddressService;
import com.bmg.mall.service.bmgOrderService;
import com.bmg.mall.util.BeanUtil;
import com.bmg.mall.util.NumberUtil;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class bmgOrderAddressServiceImpl implements bmgOrderAddressService {

    @Autowired
    private bmgOrderMapper BmgOrderMapper;
    @Autowired
    private bmgOrderItemMapper BmgOrderItemMapper;
    @Autowired
    private bmgShoppingCartItemMapper BmgShoppingCartItemMapper;
    @Autowired
    private bmgGoodsMapper BmgGoodsMapper;

    @Autowired
    private bmgOrderAddressMapper bmgOrderAddressMapper;

    @Override
    public PageResult getbmgOrdersPage(PageQueryUtil pageUtil) {
        List<bmgOrder> BmgOrders = BmgOrderMapper.findbmgOrderList(pageUtil);
        int total = BmgOrderMapper.getTotalbmgOrders(pageUtil);
        PageResult pageResult = new PageResult(BmgOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(bmgOrder BmgOrder) {
        bmgOrder temp = BmgOrderMapper.selectByPrimaryKey(BmgOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(BmgOrder.getTotalPrice());
            temp.setUserAddress(BmgOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (BmgOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<bmgOrder> orders = BmgOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (bmgOrder BmgOrder : orders) {
                if (BmgOrder.getIsDeleted() == 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                    continue;
                }
                if (BmgOrder.getOrderStatus() != 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (BmgOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<bmgOrder> orders = BmgOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (bmgOrder BmgOrder : orders) {
                if (BmgOrder.getIsDeleted() == 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                    continue;
                }
                if (BmgOrder.getOrderStatus() != 1 && BmgOrder.getOrderStatus() != 2) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (BmgOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<bmgOrder> orders = BmgOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (bmgOrder BmgOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (BmgOrder.getIsDeleted() == 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (BmgOrder.getOrderStatus() == 4 || BmgOrder.getOrderStatus() < 0) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (BmgOrderMapper.closeOrder(Arrays.asList(ids), bmgOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(bmgUserVO user, List<bmgShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(bmgShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(bmgShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<bmgGoods> BmgGoods = BmgGoodsMapper.selectByPrimaryKeys(goodsIds);
        Map<Long, bmgGoods> BmgGoodsMap = BmgGoods.stream().collect(Collectors.toMap(bmgGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (bmgShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!BmgGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > BmgGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(BmgGoods)) {
            if (BmgShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = BmgGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                bmgOrder BmgOrder = new bmgOrder();
                BmgOrder.setOrderNo(orderNo);
                BmgOrder.setUserId(user.getUserId());
                BmgOrder.setUserAddress(user.getAddress());
                //总价
                for (bmgShoppingCartItemVO BmgShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += BmgShoppingCartItemVO.getGoodsCount() * BmgShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    bmgException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                BmgOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                BmgOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (BmgOrderMapper.insertSelective(BmgOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<bmgOrderItem> BmgOrderItems = new ArrayList<>();
                    for (bmgShoppingCartItemVO BmgShoppingCartItemVO : myShoppingCartItems) {
                        bmgOrderItem BmgOrderItem = new bmgOrderItem();
                        //使用BeanUtil工具类将BmgShoppingCartItemVO中的属性复制到BmgOrderItem对象中
                        BeanUtil.copyProperties(BmgShoppingCartItemVO, BmgOrderItem);
                        //bmgOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        BmgOrderItem.setOrderId(BmgOrder.getOrderId());
                        BmgOrderItems.add(BmgOrderItem);
                    }
                    //保存至数据库
                    if (BmgOrderItemMapper.insertBatch(BmgOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    bmgException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                bmgException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            bmgException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public bmgOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        return null;
    }

    @Override
    public bmgOrder getbmgOrderByOrderNo(String orderNo) {
        return null;
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        return null;
    }

    @Override
    @Transactional
    public String insertAddress(bmgUserVO user, String orderNo,String address,String kuaidi) {
        //获取订单信息
        bmgOrder bmgOrder = BmgOrderMapper.selectByOrderNo(orderNo);
        address =address.replace("\n", "").trim();
        String[] list = address.split(";");
        for (String s : list) {
            String[] deil = s.split(",");
            if(deil.length!=3){
                list = null;
                break;
            }
            if(deil[1].length()!=11){
                list = null;
                break;
            }
            bmgOrderAddress bmgOrderAddress = new bmgOrderAddress();
            bmgOrderAddress.setOrderId(bmgOrder.getOrderId());
            bmgOrderAddress.setOrderNo(bmgOrder.getOrderNo());
            bmgOrderAddress.setUserId(bmgOrder.getUserId());
            bmgOrderAddress.setExpressStatus(kuaidi);
            bmgOrderAddress.setUserAddress(s);
            bmgOrderAddressMapper.insertSelective(bmgOrderAddress);
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }
}
