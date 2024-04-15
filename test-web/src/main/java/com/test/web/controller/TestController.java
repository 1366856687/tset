package com.test.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.test.common.utils.HttpUtils;
import com.test.common.utils.JsonXmlUtils;
import com.test.common.utils.SpringUtils;
import com.test.domain.entity.PbocData;
import com.test.web.event.CapReceivedEvent;
import com.test.web.event.CapResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/web")
@Slf4j
public class TestController {

    @GetMapping("/get")
    public String list(@RequestParam("name") String name) throws InterruptedException {
        PbocData p = new PbocData();
        p.setVariableName("123");
        log.info(p.getVariableName());
        for(int i =0; i<5;i++) {
            SpringUtils.applicationContext.publishEvent(new CapReceivedEvent(this, "nihao"));
            SpringUtils.applicationContext.publishEvent(new CapResponseEvent(this, "haohao"));
        }
        log.info("你好");
        Thread.sleep(3000);
        log.info("桃花");
        return "true";
    }

    @GetMapping("/webservice")
    public String webService(@RequestParam("name") String name) throws InterruptedException {
        String xml = null;
        try {
            String cardaddress = "http://localhost:8080/test/webService/TestOneService";
            String cardmethod = "messageOne";
            String nameSpace = "http://server.webservice.test.com";
            Map<String,Object> param= new HashMap<>();
            param.put("name", name);
            param.put("num", "10");
            xml = (String) HttpUtils.sendWebservicePcm(cardaddress, cardmethod, nameSpace, param,"10000");
        } catch (Exception e) {
            log.error("错误：{}",e);
        }
        return xml;
    }

    public static void main(String[] args) throws DocumentException {
        String url = new JsonXmlUtils().getClass().getResource("/app.txt").getPath();
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(url));
        String xmlStr = document.asXML();
        JSONObject xml2Map = JsonXmlUtils.xmlToJson(xmlStr);
        System.out.println(xml2Map);
        System.out.println("==============================");
        xmlStr = JsonXmlUtils.jsonToXml(xml2Map.toString());
        System.out.println(xmlStr);
    }
}
