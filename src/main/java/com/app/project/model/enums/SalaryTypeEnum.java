package com.app.project.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 薪酬结算方式枚举
 *
 * @author 
 * @from 
 */
public enum SalaryTypeEnum {

    /**
     * date-日结 week-周结 month-月结
     */

    DATE("日结", "date"),
    WEEK("周结", "week"),
    MONTH("月结", "month");

    private final String text;

    private final String value;

    SalaryTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SalaryTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SalaryTypeEnum anEnum : SalaryTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
