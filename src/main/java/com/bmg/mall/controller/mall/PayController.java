package com.bmg.mall.controller.mall;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.bmg.mall.common.Constants;
import com.bmg.mall.common.IndexConfigTypeEnum;
import com.bmg.mall.config.AlipayConfig;
import com.bmg.mall.controller.vo.bmgIndexCarouselVO;
import com.bmg.mall.controller.vo.bmgIndexCategoryVO;
import com.bmg.mall.controller.vo.bmgIndexConfigGoodsVO;
import com.bmg.mall.controller.vo.bmgUserVO;
import com.bmg.mall.dao.MallUserMapper;
import com.bmg.mall.dao.bmgPayMapper;
import com.bmg.mall.entity.MallUser;
import com.bmg.mall.entity.bmgPay;
import com.bmg.mall.service.*;
import com.bmg.mall.util.NumberUtil;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class PayController {

    @Resource
    private com.bmg.mall.service.bmgUserService bmgUserService;
    @Resource
    private MallUserMapper mallUserMapper;
    @Resource
    private bmgPayService bmgPayService;
    @Resource
    private com.bmg.mall.dao.bmgPayMapper bmgPayMapper;

    //支付回调更新状态和余额
    @GetMapping("/ispay")
    public String personalPage(HttpServletRequest request,
                               HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String outTradeNo = request.getParameter("out_trade_no"); //订单号
        String trade_no = request.getParameter("trade_no"); //交易状态
        //根据订单id获取订单信息
        bmgPay bmgPay = bmgPayMapper.selectByOutTradeNo(outTradeNo);
        //必须要有订单且未更新的，金额大于0且成功的
        if(bmgPay!=null && bmgPay.getPayType()<1 && bmgPay.getTotalAmount()>0 && trade_no!=null && user!=null){
            //更新支付状态
            bmgPay.setPayNo(trade_no);
            bmgPay.setPayType((byte) 1);
            bmgPay.setPayTime(new Date());
            bmgPay.setUpdateTime(new Date());
//            bmgPayMapper.updateByPrimaryKey(bmgPay.getPayId());
            bmgPayMapper.updateByPrimaryKeySelective(bmgPay);
            MallUser mallUser = mallUserMapper.selectByPrimaryKey(user.getUserId());
            //更新用户余额
            Integer sum = mallUser.getBalance()+bmgPay.getTotalAmount();
            mallUser.setBalance(sum);
            mallUserMapper.updateByPrimaryKey(mallUser);
            httpSession.setAttribute("mallUser",mallUser);
        }
        request.setAttribute("path", "personal");
        return "mall/personal";
    }


    @PostMapping("/applytest")
    @ResponseBody
    public Result applytest(@RequestParam("WIDtotal_amount") int WIDtotal_amount,@RequestParam("WIDbody") String WIDbody,
                            HttpSession httpSession) throws IOException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        if(user==null){
            return ResultGenerator.genFailResult("用户登录信息为空，无法充值");
        }
//        PrintWriter out = response.getWriter();
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no = new String("1111");
        //生成订单号
        String out_trade_no = NumberUtil.genOrderNo();
        //付款金额，必填
        int total_amount = WIDtotal_amount;
        //订单名称，必填
        String subject = new String("GOGOGO商城充值");
        //商品描述，可空
        String body = WIDbody;

        //创建订单记录
        bmgPayService.insertPayOrder(user.getUserId(),out_trade_no,total_amount,body);


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
        return ResultGenerator.genSuccessResult((Object)form);
    }



    //我的充值记录
    @GetMapping("/history")
    public String historyListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", bmgPayService.getMyhistory(pageUtil));
        request.setAttribute("path", "history");
        return "mall/my-history";
    }
}
