package com.kjz.admin.global.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ActionTypeEnum {


    UNKNOWN(0, "未知"),
    ADD(1, "新增"),
    MODIFY(2, "修改"),
    DELETE(3, "删除"),
    EXPORT(4, "导出"),
    ;
    private Integer type;
    private String desc;

    public static ActionTypeEnum getByType(int type) {
        return Arrays.stream(values()).filter(actionTypeEnum ->
                actionTypeEnum.getType().equals(type)).findFirst().orElse(UNKNOWN);
    }
}
