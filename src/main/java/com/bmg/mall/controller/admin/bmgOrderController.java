package com.bmg.mall.controller.admin;

import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.controller.vo.bmgOrderItemVO;
import com.bmg.mall.dao.bmgOrderAddressMapper;
import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.entity.bmgPay;
import com.bmg.mall.service.bmgOrderAddressService;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
import com.bmg.mall.service.bmgOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class bmgOrderController {

    @Resource
    private bmgOrderService bmgOrderService;
    @Resource
    private bmgOrderAddressMapper BmgOrderAddressMapper;

    @GetMapping("/orders")
    public String ordersPage(HttpServletRequest request) {
        request.setAttribute("path", "orders");
        return "admin/bmg_order";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/orders/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(bmgOrderService.getbmgOrdersPage(pageUtil));
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/orders/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody bmgOrder bmgOrder) {
//        if (Objects.isNull(bmgOrder.getTotalPrice())
//                || Objects.isNull(bmgOrder.getOrderId())
//                || bmgOrder.getOrderId() < 1
//                || bmgOrder.getTotalPrice() < 1
//                || StringUtils.isEmpty(bmgOrder.getUserAddress())) {
//            return ResultGenerator.genFailResult("参数异常！");
//        }
        if (Objects.isNull(bmgOrder.getTotalPrice())
                || Objects.isNull(bmgOrder.getOrderId())
                || bmgOrder.getOrderId() < 1
                || bmgOrder.getTotalPrice() < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = bmgOrderService.updateOrderInfo(bmgOrder);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/order-items/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        List<bmgOrderItemVO> orderItems = bmgOrderService.getOrderItems(id);
        if (!CollectionUtils.isEmpty(orderItems)) {
            return ResultGenerator.genSuccessResult(orderItems);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    /**
     * 配货
     */
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.POST)
    @ResponseBody
    public Result checkDone(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = bmgOrderService.checkDone(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     */
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.POST)
    @ResponseBody
    public Result checkOut(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = bmgOrderService.checkOut(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.POST)
    @ResponseBody
    public Result closeOrder(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = bmgOrderService.closeOrder(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 查看收件信息
     */
    @GetMapping("/order-address/{id}")
    @ResponseBody
    public Result addressInfo(@PathVariable("id") Long id) {
        List<bmgOrderAddress> bmgOrderAddresses = BmgOrderAddressMapper.selectByOrderId(id);
        if (!CollectionUtils.isEmpty(bmgOrderAddresses)) {
            return ResultGenerator.genSuccessResult(bmgOrderAddresses);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }


    /**
     * 修改物流信息
     */
    @RequestMapping(value = "/pays/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody bmgOrderAddress bmgOrderAddress) {
        if (Objects.isNull(bmgOrderAddress.getAddressId())
                || Objects.isNull(bmgOrderAddress.getExpressNumber())
                || Objects.isNull(bmgOrderAddress.getExpressStatus())
                ) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = bmgOrderService.updateOrderAddress(bmgOrderAddress);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
}
