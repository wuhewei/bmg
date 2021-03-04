package com.bmg.mall.service.impl;

import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.common.bmgException;
import com.bmg.mall.common.bmgOrderStatusEnum;
import com.bmg.mall.controller.vo.*;
import com.bmg.mall.dao.*;
import com.bmg.mall.entity.*;
import com.bmg.mall.service.bmgOrderAddressService;
import com.bmg.mall.service.bmgPayService;
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
public class bmgPayServiceImpl implements bmgPayService {


    @Autowired
    private bmgPayMapper bmgPayMapper;
    @Autowired
    private MallUserMapper mallUserMapper;



    @Override
    @Transactional
    public String insertPayOrder(Long userid, String outTradeNo,int totalAmount,String remark) {
        bmgPay bmgPay = new bmgPay();
        bmgPay.setUserId(userid);
        bmgPay.setOutTradeNo(outTradeNo);
        bmgPay.setTotalAmount(totalAmount);
        bmgPay.setRemark(remark);
        bmgPayMapper.insertSelective(bmgPay);
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public PageResult getMyhistory(PageQueryUtil pageUtil) {
        int total = bmgPayMapper.getTotalbmgPay(pageUtil);
        List<bmgPay> BmgPays = bmgPayMapper.findbmgPayList(pageUtil);
        PageResult pageResult = new PageResult(BmgPays, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult getbmgPaysPage(PageQueryUtil pageUtil) {
        List<bmgPay> BmgPays = bmgPayMapper.findbmgPayList(pageUtil);
        List<bmgPayVO> bmgPayVO = new ArrayList<>();
        if (!CollectionUtils.isEmpty(BmgPays)) {
            bmgPayVO = BeanUtil.copyList(BmgPays, bmgPayVO.class);
        }
        for (bmgPayVO bb:bmgPayVO) {
            MallUser mallUser = mallUserMapper.selectByPrimaryKey(bb.getUserId());
            bb.setUser(mallUser.getNickName());
            bb.setUsercount(mallUser.getLoginName());
        }
        int total = bmgPayMapper.getTotalbmgPay(pageUtil);

        PageResult pageResult = new PageResult(bmgPayVO, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
