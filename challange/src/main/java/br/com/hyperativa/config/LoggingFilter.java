package br.com.hyperativa.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        var request = (HttpServletRequest) req;
        var response = (HttpServletResponse) res;
        long start = System.currentTimeMillis();
        chain.doFilter(req, res);
        long time = System.currentTimeMillis() - start;
        log.info("{} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), time);
    }
}
