package com.bmg.mall.controller.vo;

import lombok.Data;

/**
 * @author hewei
 * @date 2021/3/4
 */
@Data
public class AddressImportParsingVo {

    /**
     * 收货人
     */
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 收货地址
     */
    private String address;
}
