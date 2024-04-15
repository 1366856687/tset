package com.test.webservice.facade;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


@WebService(name = "TestService", // 暴露服务名称
        targetNamespace = "http://server.webservice.test.com"// 命名空间,一般是接口的包名倒序
)
public interface TestWebService {

    @WebMethod
    public String message(@WebParam(name = "name") String name);

    @WebMethod
    public String message1(@WebParam(name = "name1") String name);
}
