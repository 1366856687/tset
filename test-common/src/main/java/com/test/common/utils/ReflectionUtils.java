package com.test.common.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class ReflectionUtils {

    /*
    * 支持父类赋值
    **/
    public static void defaultValue(Object o) throws IllegalAccessException {
        Class tmpClazz = o.getClass();
        while (tmpClazz != null) {
            if (tmpClazz.equals(Object.class)) {
                tmpClazz = tmpClazz.getSuperclass();
                continue;
            }
            Field[] fields = tmpClazz.getDeclaredFields();
            for (Field field : fields) {
                //有的字段是用private修饰的 将他设置为可读
                field.setAccessible(true);
                if (field.getType().toString().equals("class java.lang.String") && field.get(o) == null) {
                    field.set(o, "-99999");
                }
                if (field.getType().toString().equals("class java.lang.Integer") && field.get(o) == null) {
                    field.set(o, -99999);
                }
                if (field.getType().toString().equals("class java.math.BigDecimal") && field.get(o) == null) {
                    field.set(o, new BigDecimal(-99999));
                }
            }
            tmpClazz = tmpClazz.getSuperclass();
        }
    }

}
