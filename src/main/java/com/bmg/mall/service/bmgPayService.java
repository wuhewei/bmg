package com.bmg.mall.service;

import com.bmg.mall.controller.vo.bmgOrderDetailVO;
import com.bmg.mall.controller.vo.bmgShoppingCartItemVO;
import com.bmg.mall.controller.vo.bmgUserVO;
import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;

import java.util.List;

public interface bmgPayService {


    /**
     * 添加充值记录
     *
     * @return
     */
    String insertPayOrder(Long userid, String outTradeNo,int totalAmount,String remark);

    /**
     * 我的充值记录列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyhistory(PageQueryUtil pageUtil);

    /**
     * 充值记录
     * @param pageUtil
     * @return
     */
    PageResult getbmgPaysPage(PageQueryUtil pageUtil);
}
