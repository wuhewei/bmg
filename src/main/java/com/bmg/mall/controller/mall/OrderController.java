package com.bmg.mall.controller.mall;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.StreamUtil;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.bmg.mall.common.Constants;
import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.common.bmgException;
import com.bmg.mall.config.AlipayConfig;
import com.bmg.mall.config.KdGoldAPIDemo;
import com.bmg.mall.controller.vo.bmgOrderDetailVO;
import com.bmg.mall.controller.vo.bmgShoppingCartItemVO;
import com.bmg.mall.controller.vo.bmgUserVO;
import com.bmg.mall.dao.MallUserMapper;
import com.bmg.mall.dao.bmgOrderAddressMapper;
import com.bmg.mall.entity.MallUser;
import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.service.bmgOrderAddressService;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
import com.bmg.mall.service.bmgOrderService;
import com.bmg.mall.service.bmgShoppingCartService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    private bmgShoppingCartService BmgShoppingCartService;
    @Resource
    private bmgOrderService BmgOrderService;
    @Resource
    private bmgOrderAddressService bmgOrderAddressService;
    @Resource
    private MallUserMapper mallUserMapper;
    @Resource
    private bmgOrderAddressMapper BmgOrderAddressMapper;


    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        bmgOrderDetailVO orderDetailVO = BmgOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", BmgOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @PostMapping("/saveOrder")
    @ResponseBody
    public String saveOrder(HttpSession httpSession, @RequestParam(value= "count") int count,
                            @RequestParam(value= "total") int total, @RequestParam(value= "address") String address,
    @RequestParam(value= "kuaidi") String kuaidi) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<bmgShoppingCartItemVO> myShoppingCartItems = BmgShoppingCartService.getMyShoppingCartItems(user.getUserId());
//        if (StringUtils.isEmpty(user.getAddress().trim())){
//            //无收货地址
//            bmgException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
//        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = BmgOrderService.saveOrder(user, myShoppingCartItems,total,count);
        if(kuaidi.equals("YZ")){
            kuaidi = "邮政EMS";
        }else {
            kuaidi = "韵达快递";
        }
        bmgOrderAddressService.insertAddress(user,saveOrderResult,address,kuaidi);
        //跳转到订单详情页
        return "/orders/" + saveOrderResult;
    }

//    @GetMapping("/saveOrder")
//    public String saveOrder(HttpSession httpSession) {
//        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
//        List<bmgShoppingCartItemVO> myShoppingCartItems = BmgShoppingCartService.getMyShoppingCartItems(user.getUserId());
//        if (StringUtils.isEmpty(user.getAddress().trim())) {
//            //无收货地址
//            bmgException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
//        }
//        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
//            //购物车中无数据则跳转至错误页
//            bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
//        }
//        //保存订单并返回订单号
//        String saveOrderResult = BmgOrderService.saveOrder(user, myShoppingCartItems);
//        //跳转到订单详情页
//        return "redirect:/orders/" + saveOrderResult;
//    }

    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = BmgOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = BmgOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        bmgOrder BmgOrder = BmgOrderService.getbmgOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", BmgOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        bmgOrder BmgOrder = BmgOrderService.getbmgOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        MallUser mallUser = mallUserMapper.selectByPrimaryKey(user.getUserId());
        request.setAttribute("balance",mallUser.getBalance());
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", BmgOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/alipay";
        }
    }

    //订单支付
    @GetMapping("/paySuccess")
    @ResponseBody
    @Transactional
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType, HttpSession httpSession) throws Exception {
        String payResult = BmgOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
//            bmgOrderAddressService.insertAddress(user,orderNo);
            MallUser mallUser = mallUserMapper.selectByPrimaryKey(user.getUserId());
            bmgOrder bmgOrder = BmgOrderService.getbmgOrderByOrderNo(orderNo);
            Integer yue = mallUser.getBalance()- bmgOrder.getTotalPrice();
            if(yue<=0){
                return ResultGenerator.genFailResult("余额不足，请充值后再使用");
            }
            mallUser.setBalance(yue);
            mallUserMapper.updateByPrimaryKey(mallUser);

            //获取该订单下的收件地址
            List<bmgOrderAddress> bmgOrderAddresses = BmgOrderAddressMapper.selectByOrderId(bmgOrder.getOrderId());
            //生成电子面单
            KdGoldAPIDemo kdGoldAPIDemo = new KdGoldAPIDemo();
            //更新订单物流信息
            for(bmgOrderAddress bm : bmgOrderAddresses){
                String resul = kdGoldAPIDemo.orderOnlineByJson(bm);
                JSONObject jsonObject = JSONObject.parseObject(resul);
                JSONObject order = (JSONObject) jsonObject.get("Order");
                String LogisticCode = (String) order.get("LogisticCode");
                if(order!=null && LogisticCode!=null){
                    System.out.println(LogisticCode);
                    bm.setExpressNumber(LogisticCode);
                    BmgOrderAddressMapper.updateByPrimaryKeySelective(bm);
                }
            }
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

    @GetMapping("/paySuccess1")
//    @RequestMapping(value = "/paySuccess1", produces = "text/html; charset=UTF-8",method= RequestMethod.GET)
    @ResponseBody
    public Result paySuccess1(HttpServletResponse response, HttpServletRequest request, @RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType, HttpSession httpSession) throws IOException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

//        PrintWriter out = response.getWriter();
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = new String(orderNo);
        //付款金额，必填
        String total_amount = new String("1000");
        //订单名称，必填
        String subject = new String("335555");
        //商品描述，可空
        String body = new String("555666");

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest,"get").getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println(form);
//        out.println(form);
        return ResultGenerator.genSuccessResult((Object)form);
//        response.getWriter().write(form);//直接将完整的表单html输出到页面
//        response.getWriter().flush();
//        response.getWriter().close();
    }
}
