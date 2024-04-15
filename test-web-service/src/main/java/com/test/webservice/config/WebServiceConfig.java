package com.test.webservice.config;

import com.test.webservice.facade.TestOneWebService;
import com.test.webservice.facade.TestWebService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.xml.ws.Endpoint;

@Configuration
public class WebServiceConfig {
    @Resource
    private TestWebService testService;

    @Resource
    private TestOneWebService testOneService;

    @Bean
    public ServletRegistrationBean disServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CXFServlet(), "/webService/*");
        return servletRegistrationBean;
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), testService);
        endpoint.publish("/TestService");
        return endpoint;
    }
    @Bean
    public Endpoint endpoint1() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), testOneService);
        endpoint.publish("/TestOneService");
        return endpoint;
    }

}
