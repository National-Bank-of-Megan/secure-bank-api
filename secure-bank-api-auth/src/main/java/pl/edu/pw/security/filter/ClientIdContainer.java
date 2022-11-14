package pl.edu.pw.security.filter;

import org.springframework.web.filter.OncePerRequestFilter;

public abstract class ClientIdContainer extends OncePerRequestFilter {

    protected static String clientId;
}
