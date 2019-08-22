package com.bee.yunkong.core;

/**
 * Created by yuanshenghong on 2017/3/18.
 * 所有存到sp的键必需在这里注册，避免重复
 * 注意:!请保持name与注册的key一致
 */

public enum SpConstant {
    phoneNum("phoneNum"),
    ;

    private String name;

    private SpConstant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
