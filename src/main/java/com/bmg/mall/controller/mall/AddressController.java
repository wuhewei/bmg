package com.bmg.mall.controller.mall;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.bmg.mall.controller.vo.AddressImportParsingVo;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author hewei
 * @date 2021/3/4
 */
@Controller
@RequestMapping(value = "/address")
public class AddressController {

    @ResponseBody
    @PostMapping(value = "/import/parsing")
    public Result importParsing(@RequestParam("file") MultipartFile file) throws IOException {
        try (ExcelReader reader = ExcelUtil.getReader(file.getInputStream(), 0)) {
            return ResultGenerator.genSuccessResult(reader.readAll(AddressImportParsingVo.class));
        }
    }

}
