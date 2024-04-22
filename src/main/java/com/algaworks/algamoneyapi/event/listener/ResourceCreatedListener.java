package com.algaworks.algamoneyapi.event.listener;

import com.algaworks.algamoneyapi.event.ResourceCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@Component
public class ResourceCreatedListener implements ApplicationListener<ResourceCreatedEvent> {

    @Override
    public void onApplicationEvent(ResourceCreatedEvent event) {
        HttpServletResponse response = event.getResponse();
        String code = event.getCode();
        addHeaderLocation(response, code);
    }

    private static void addHeaderLocation(HttpServletResponse response, String code) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{code}")
                .buildAndExpand(code).toUri();
        response.setHeader("Location", uri.toASCIIString());
    }
}
