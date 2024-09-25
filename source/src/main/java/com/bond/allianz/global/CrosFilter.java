package com.bond.allianz.global;


import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrosFilter implements Filter  {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        //res.addHeader("Referrer-Policy", "no-referrer");
        //res.addHeader("Content-Security-Policy", "default-src style-src  'unsafe-inline' 'self'");
        //default-src 'unsafe-inline' 'self'
        //res.addHeader("Content-Security-Policy", "default-src *; style-src 'self' 'unsafe-inline';script-src 'self' 'unsafe-inline' 'unsafe-eval'; img-src  * data: 'unsafe-eval'; frame-src  *;object-src data: 'unsafe-eval'");
        //res.addHeader("Feature-Policy", "camera 'none'; microphone 'none'");
        //res.addHeader("Permissions-Policy", "camera=(),microphonee=()");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
