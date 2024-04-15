package com.test.web.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class CapResponseEvent extends ApplicationEvent {

    private String message;

    public CapResponseEvent(Object source,String message) {
        super(source);
        this.message = message;
    }
}
