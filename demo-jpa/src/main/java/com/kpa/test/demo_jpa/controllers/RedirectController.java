package com.kpa.test.demo_jpa.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class RedirectController {

    @GetMapping({"", "/", "/api"})
    public void redirectToSwagger(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder redirectUrl = new StringBuilder();
        redirectUrl.append(request.getScheme())
            .append("://")
            .append(request.getServerName())
            .append(":")
            .append(request.getServerPort())
            .append("/swagger-ui/index.html");
        response.sendRedirect(redirectUrl.toString());
    }
}
