package com.test.webservice.facade.impl;

import com.test.webservice.facade.TestWebService;
import org.springframework.stereotype.Service;

import javax.jws.WebService;


@WebService(serviceName = "TestWebService",
        targetNamespace = "http://server.webservice.test.com",
        endpointInterface = "com.test.webservice.facade.TestWebService"
)
@Service
public class TestWebServiceImpl implements TestWebService {

    @Override
    public String message(String name) {
        return  "=====Hello! " + name + "=====";
    }

    @Override
    public String message1(String name) {
        return "=====Hello1! " + name + "=====";
    }

}
