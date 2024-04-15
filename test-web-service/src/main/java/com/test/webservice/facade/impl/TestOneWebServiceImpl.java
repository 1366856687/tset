package com.test.webservice.facade.impl;

import com.test.webservice.facade.TestOneWebService;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@WebService(serviceName = "TestOneWebService", // 与接口中指定的name一致
        targetNamespace = "http://server.webservice.test.com", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.test.webservice.facade.TestOneWebService"// 接口地址
)
@Service
public class TestOneWebServiceImpl implements TestOneWebService {
    @Override
    public String messageOne(String name, int num) {
        return "=====HelloOne! " + name + "=====" + num;
    }
}
