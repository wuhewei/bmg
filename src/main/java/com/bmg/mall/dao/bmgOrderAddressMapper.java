package com.bmg.mall.dao;

import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface bmgOrderAddressMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(bmgOrderAddress record);

    int insertSelective(bmgOrderAddress record);

    bmgOrderAddress selectByPrimaryKey(Long orderId);

    bmgOrderAddress selectByOrderNo(String orderNo);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<bmgOrderAddress> selectByOrderId(Long orderId);

    //更新物流信息
    int updateByPrimaryKeySelective(bmgOrderAddress record);
}
