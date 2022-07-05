package pl.edu.pw.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/account/combination")
public class DevicesFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DevicesFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("DevicesFilter->\treceiving login...");

//        todo check if new ip address
//        new? send email

    }
}
