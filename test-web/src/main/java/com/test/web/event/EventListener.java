package com.test.web.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventListener implements ApplicationListener {

    @Override
    @Async
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof CapResponseEvent) {
            log.info("1");
            log.info(((CapResponseEvent) applicationEvent).getMessage());
        } else if (applicationEvent instanceof CapReceivedEvent) {
            log.info("2");
            log.info(((CapReceivedEvent) applicationEvent).getCapxml());
        }

    }
}
