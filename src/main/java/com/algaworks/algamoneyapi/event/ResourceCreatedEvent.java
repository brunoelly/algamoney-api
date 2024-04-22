package com.algaworks.algamoneyapi.event;

import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;

public class ResourceCreatedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private HttpServletResponse response;
    private String code;

    public ResourceCreatedEvent(Object source, HttpServletResponse response, String code) {
        super(source);
        this.response = response;
        this.code = code;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getCode() {
        return code;
    }
}
