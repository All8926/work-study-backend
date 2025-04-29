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
public enum JobApplicationStatusEnum {
    /**
     * 0-待审核 1-审核拒绝 2-待面试 3-面试不通过 4-面试通过
     */
    IN_REVIEW("审核中", 0),
    REJECTED("审核拒绝", 1),
    IN_INTERVIEW("待面试", 2),
    INTERVIEW_FAILED("面试不通过", 3),
    INTERVIEW_PASSED("面试通过", 4);


    private final String text;

    private final Integer value;

    JobApplicationStatusEnum(String text, Integer value) {
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
    public static JobApplicationStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JobApplicationStatusEnum anEnum : JobApplicationStatusEnum.values()) {
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
