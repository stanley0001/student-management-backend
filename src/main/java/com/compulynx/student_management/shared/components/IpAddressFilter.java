package com.compulynx.student_management.shared.components;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IpAddressFilter implements Filter {

    private static final ThreadLocal<String> ipAddressHolder = new ThreadLocal<>();

    public static String getIpAddress() {
        return ipAddressHolder.get();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ipAddressHolder.set(request.getRemoteAddr());
        try {
            chain.doFilter(request, response);
        } finally {
            ipAddressHolder.remove();
        }
    }
}

