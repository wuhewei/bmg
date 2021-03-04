package com.bmg.mall.dao;

import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.entity.bmgPay;
import com.bmg.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface bmgPayMapper {

    int insertSelective(bmgPay record);

    bmgPay selectByOutTradeNo(String outTradeNo);

    int updateByPrimaryKey(Long payId); //更新支付状态

    int updateByPrimaryKeySelective(bmgPay record); //更新充值订单

    int getTotalbmgPay(PageQueryUtil pageUtil); //记录总数

    List<bmgPay> findbmgPayList(PageQueryUtil pageUtil); //分页数据
}
