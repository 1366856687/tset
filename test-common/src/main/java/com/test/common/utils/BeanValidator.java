package com.test.common.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.common.exception.BizException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class BeanValidator {
    private static ValidatorFactory validatorFactory= Validation.buildDefaultValidatorFactory();
    //返回map
    public static <T> Map<String,String> validate(T t, Class... groups){
        Validator validator=validatorFactory.getValidator();
        Set validateResult=validator.validate(t,groups);
        //如果为空
        if (validateResult.isEmpty()){
            return Collections.emptyMap();
        }else{
            //不为空时表示有错误
            LinkedHashMap errors= Maps.newLinkedHashMap();
            //遍历
            Iterator iterator=validateResult.iterator();
            while (iterator.hasNext()){
                ConstraintViolation violation=(ConstraintViolation) iterator.next();
                errors.put(violation.getPropertyPath().toString(),violation.getMessage());
            }
            return errors;
        }
    }

    //返回list
    public static Map<String,String> validateList(Collection<?> collection){
        //基础校验collection是否为空
        Preconditions.checkNotNull(collection);
        //遍历collection
        Iterator iterator=collection.iterator();
        Map errors;
        do {
            //如果循环下一个为空直接返回空
            if (!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object=iterator.next();
            errors=validate(object,new Class[0]);
        }while (errors.isEmpty());
        return errors;
    }

    // 校验某一对象是否合法
    public static Map<String,String> validateObject(Object first,Object... objects){
        if (objects !=null && objects.length > 0 ){
            return validateList(Lists.asList(first,objects));
        } else {
            return validate(first , new Class[0]);
        }
    }

    //校验参数方法
    public static void check(Object param) throws BizException {
        Map<String,String> map=BeanValidator.validateObject(param);
        //如果错误集合map不为空则抛出异常
        if (!map.isEmpty()){
            throw  new BizException(map.toString());
        }
    }
}
