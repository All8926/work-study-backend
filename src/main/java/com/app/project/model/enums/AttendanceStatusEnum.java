package com.app.project.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位状态枚举
 *
 * @author 
 * @from 
 */
public enum AttendanceStatusEnum {
    /**
     * 0-正常 1-迟到 2-早退 3-旷工 4-请假
     */
    NORMAL("正常", 0),
    LATE("迟到", 1),
    LEAVE_EARLY("早退", 2),
    ABSENTEEISM("旷工", 3),
    LEAVE("请假", 4);


    private final String text;

    private final Integer value;

    AttendanceStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static AttendanceStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (AttendanceStatusEnum anEnum : AttendanceStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
