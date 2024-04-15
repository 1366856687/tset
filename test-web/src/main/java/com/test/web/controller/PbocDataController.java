package com.test.web.controller;

import com.alibaba.fastjson.JSON;
import com.test.common.exception.BizException;
import com.test.common.utils.BeanValidator;
import com.test.service.PbocDataService;
import com.test.common.entity.BaseResult;
import com.test.webapi.dto.input.GetPbocDataListReqDto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/web")
@Slf4j
public class PbocDataController {

    @Resource
    private PbocDataService pbocDataService;

    @Resource
    private RedissonClient redisson;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/pbocdata/list")
    public BaseResult getPbocDatalist(GetPbocDataListReqDto inputDto) {
        String lockKey = "123456";
        RLock lock = redisson.getLock(lockKey); //获取锁
        try {
            lock.lock();    //上锁
            log.info("锁已开启");
            redisTemplate.opsForValue().set("1234", inputDto);
            GetPbocDataListReqDto d = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get("1234")),GetPbocDataListReqDto.class);
            log.info("查询:{}",JSON.toJSONString(d));
            Thread.sleep(40000);
            BeanValidator.check(inputDto);
        } catch (BizException e) {
            return BaseResult.fail(e.getMsg());
        } catch (InterruptedException e) {
            return BaseResult.fail(e.getMessage());
        }finally {
            lock.unlock();    //删除锁
            log.info("锁已关闭");
        }
        return BaseResult.success(pbocDataService.getPbocDataListPage(inputDto));
    }

    @GetMapping("/pbocdata/list1")
    public BaseResult getPbocDatalist1(GetPbocDataListReqDto inputDto) {
        String lockKey = "12345";
        RLock lock = redisson.getLock(lockKey); //获取锁
        try {
            lock.lock();    //上锁
            log.info("锁已开启");
            redisTemplate.opsForValue().set("1234", inputDto);
            GetPbocDataListReqDto d = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get("1234")),GetPbocDataListReqDto.class);
            log.info("查询:{}",JSON.toJSONString(d));
            Thread.sleep(40000);
            BeanValidator.check(inputDto);
        } catch (BizException e) {
            return BaseResult.fail(e.getMsg());
        } catch (InterruptedException e) {
            return BaseResult.fail(e.getMessage());
        }finally {
            lock.unlock();    //删除锁
            log.info("锁已关闭");
        }
        return BaseResult.success(pbocDataService.getPbocDataListPage(inputDto));
    }
}
