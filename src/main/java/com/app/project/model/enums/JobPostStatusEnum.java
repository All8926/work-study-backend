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
public enum JobPostStatusEnum {
    /**
     * 0-待审核 1-已发布 2-已下线 3-审核不通过
     */
    IN_REVIEW("审核中", 0),
    HAVE_PUBLISHED("已发布", 1),
    NOT_PUBLISHED("已下线", 2),
    REJECTED("审核不通过", 3);


    private final String text;

    private final Integer value;

    JobPostStatusEnum(String text, Integer value) {
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
    public static JobPostStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JobPostStatusEnum anEnum : JobPostStatusEnum.values()) {
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
