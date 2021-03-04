package com.bmg.mall.controller.admin;

import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.controller.vo.bmgOrderItemVO;
import com.bmg.mall.dao.bmgOrderAddressMapper;
import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderAddress;
import com.bmg.mall.entity.bmgPay;
import com.bmg.mall.service.bmgOrderService;
import com.bmg.mall.service.bmgPayService;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
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
public class bmgPayController {

    @Resource
    private com.bmg.mall.service.bmgPayService bmgPayService;
    @Resource
    private bmgOrderAddressMapper BmgOrderAddressMapper;

    @GetMapping("/pays")
    public String paysPage(HttpServletRequest request) {
        request.setAttribute("path", "pays");
        return "admin/bmg_pay";
    }

    /**
     * 充值记录列表
     */
    @RequestMapping(value = "/pays/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(bmgPayService.getbmgPaysPage(pageUtil));
    }

}
