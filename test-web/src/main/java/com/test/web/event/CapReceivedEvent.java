package com.test.web.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class CapReceivedEvent extends ApplicationEvent {
    private String capxml;

    public CapReceivedEvent(Object source,String capxml) {
        super(source);
        this.capxml = capxml;
    }

}
