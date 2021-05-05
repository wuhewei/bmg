package com.bmg.mall.controller.mall;

import com.bmg.mall.common.Constants;
import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.config.KdGoldAPIDemo;
import com.bmg.mall.controller.vo.bmgUserVO;
import com.bmg.mall.dao.MallUserMapper;
import com.bmg.mall.entity.MallUser;
import com.bmg.mall.util.MD5Util;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
import com.bmg.mall.service.bmgUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class PersonalController {

    @Resource
    private bmgUserService bmgUserService;
    @Resource
    private MallUserMapper mallUserMapper;

    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request,
                               HttpSession httpSession) {
        request.setAttribute("path", "personal");
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser mallUser = mallUserMapper.selectByPrimaryKey(user.getUserId());
        httpSession.setAttribute("mallUser",mallUser);
        return "mall/personal";
    }

    @GetMapping("/alipay1")
    public String alipay1(HttpServletRequest request,
                               HttpSession httpSession) {
//        request.setAttribute("path", "personal");
//        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
//        MallUser mallUser = mallUserMapper.selectByPrimaryKey(user.getUserId());
//        httpSession.setAttribute("mallUser",mallUser);
        return "mall/alipay1";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }

    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "mall/login";
    }

    @GetMapping({"/register", "register.html"})
    public String registerPage() {
        return "mall/register";
    }

    @GetMapping("/personal/addresses")
    public String addressesPage() {
        return "mall/addresses";
    }

    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("verifyCode") String verifyCode,
                        @RequestParam("password") String password,
                        HttpSession httpSession) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String loginResult = bmgUserService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        //登录成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("verifyCode") String verifyCode,
                           @RequestParam("password") String password,
                           HttpSession httpSession) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String registerResult = bmgUserService.register(loginName, password);
        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody MallUser mallUser, HttpSession httpSession) {
        String address = mallUser.getAddress().replace("\n", "").trim();
        String[] list = address.split(";");
        String erro="";
        if (list == null || list.length<1) {
            Result result = ResultGenerator.genFailResult(erro+"地址为空或地址填写错误");
            return result;
        }

        for (String s : list) {
            String[] deil = s.split(",");
            erro=deil[0];
            if(deil.length!=3 || deil[1].length()!=11){
                Result result = ResultGenerator.genFailResult(erro+"的信息或手机号码填写错误,请检查后校验");
                return result;
            }
            String addressDeail = deil[2];
            Map<String,String> map = KdGoldAPIDemo.addressResolution(addressDeail);
            KdGoldAPIDemo kdGoldAPIDemo = new KdGoldAPIDemo();

            if(kdGoldAPIDemo.isNull(map.get("province")) || kdGoldAPIDemo.isNull(map.get("city")) || kdGoldAPIDemo.isNull(map.get("county")) ){
                Result result = ResultGenerator.genFailResult(erro+"的详细地址填写错误,请检查后校验");
                return result;
            }
        }
//        bmgUserVO mallUserTemp = bmgUserService.updateUserInfo(mallUser, httpSession);
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            result.setData(list);
            return result;
    }


    @PostMapping("/personal/updateUser")
    @ResponseBody
    public Result updateUser(@RequestBody MallUser mallUser, HttpSession httpSession) {
        MallUser mallUser1 = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
        if(mallUser.getNickName()!=null){
            mallUser1.setNickName(mallUser.getNickName());
            mallUser1.setIntroduceSign(mallUser.getIntroduceSign());
        }
        bmgUserVO mallUserTemp = bmgUserService.updateUserInfo(mallUser1, httpSession);
        Result result = ResultGenerator.genSuccessResult();
        return result;
    }
}
