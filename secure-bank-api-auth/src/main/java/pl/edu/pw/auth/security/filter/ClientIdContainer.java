package pl.edu.pw.auth.security.filter;

import org.springframework.web.filter.OncePerRequestFilter;

public abstract class ClientIdContainer extends OncePerRequestFilter {

    protected static String clientId;
}
