package com.test.webservice.facade;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


@WebService(name = "TestOneService", // 暴露服务名称
        targetNamespace = "http://server.webservice.test.com"// 命名空间,一般是接口的包名倒序
)
public interface TestOneWebService {

    @WebMethod
    public String messageOne(@WebParam(name = "name") String name, @WebParam(name = "num") int num);
}
