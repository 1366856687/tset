package com.test.common.exception;

import com.test.common.enums.RespCodeEnum;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * BizExceptionUtils
 *
 * @author lvbiao8
 * @date 2021-06-14 10:51
 */
public class BizExceptionUtils {
    /**
     * 占位符起始字符
     */
    private static final char START_PLACE = '{';
    /**
     * 占位符结束字符
     */
    private static final char END_PLACE = '}';


    private BizExceptionUtils() {
    }

    /**
     * 检查基础参数
     *
     * @param obj
     * @param message
     * @param param
     */
    public static void checkNotEmpty(Object obj, String message, Object... param) {
        if (isEmpty(obj)) {
            throw new BizException(RespCodeEnum.FAILURE.getCode(), replacePlaceholder(message, param));
        }
    }

    /**
     * 自定义日志{}占位符替换
     * Example : formate="I am {}", param="tom" return "I am tom"
     *
     * @param format
     * @param param
     * @return
     */
    public static String replacePlaceholder(String format, Object... param) {
        if (format == null || format.length() == 0 || param == null) {
            return format;
        }
        char[] chars = format.toCharArray();
        StringBuilder sb = new StringBuilder(format.length());
        int length = chars.length;
        int paramIndex = 0;
        int paramLength = param.length;
        for (int i = 0; i < length; i++) {
            char currentChar = chars[i];
            int nextIndex = i + 1;
            if (currentChar == START_PLACE
                    && nextIndex < length
                    && chars[nextIndex] == END_PLACE
                    && paramIndex < paramLength) {
                sb.append(param[paramIndex]);
                paramIndex++;
                i++;
            } else {
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

    /**
     * 检查对象是否为空
     *
     * @param object
     * @return
     */
    private static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        return false;
    }

}
