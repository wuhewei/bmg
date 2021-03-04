package com.bmg.mall.controller.mall;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.bmg.mall.controller.vo.AddressImportParsingVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hewei
 * @date 2021/3/4
 */
@Controller
@RequestMapping(value = "/address")
public class AddressController {

    @ResponseBody
    @PostMapping(value = "/import/parsing")
    public List<AddressImportParsingVo> importParsing(@RequestParam("file") MultipartFile file) throws IOException {
        try (ExcelReader reader = ExcelUtil.getReader(file.getInputStream(), 0)) {
            List<Map<String, Object>> list = reader.readAll();
            for (Map<String, Object> map : list) {
                String account = map.get("账号").toString();
                if (StrUtil.isNotBlank(account)) {
//                    accounts.add(account);
                }
            }
        }

        return new ArrayList<>();
    }

}
