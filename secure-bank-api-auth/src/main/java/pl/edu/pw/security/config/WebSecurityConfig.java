package pl.edu.pw.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.edu.pw.repository.AccountHashRepository;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.security.filter.AuthorizationFilter;
import pl.edu.pw.security.filter.MobileAuthenticationFilter;
import pl.edu.pw.security.filter.WebAuthenticationFilter;
import pl.edu.pw.service.devices.DevicesServiceImpl;
import pl.edu.pw.util.JWTUtil;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig {

    private AuthenticationConfiguration authenticationConfiguration;
    private RestAuthenticationSuccessHandler successHandler;
    private RestAuthenticationFailureHandler failureHandler;
    private AccountRepository accountRepository;
    private AccountHashRepository accountHashRepository;
    private DevicesServiceImpl devicesService;
    private JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        desktop app processing
        WebAuthenticationFilter webAuthenticationFilter = getAuthenticationFilter();
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(accountRepository, jwtUtil.getJwtSecret());

//        mobile app processing
        MobileAuthenticationFilter mobileAuthenticationFilter = new MobileAuthenticationFilter(authenticationManagerBean(authenticationConfiguration), accountRepository, accountHashRepository);
        mobileAuthenticationFilter.setFilterProcessesUrl("/api/mobile/login");

        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/api/web/login/**", "/api/mobile/login/**", "/api/web/login/verify/**", "/api/web/token/refresh").permitAll()
                .and()
                .authorizeRequests().antMatchers("/api/web/register/**").permitAll()
                .and()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilter(webAuthenticationFilter)
                .addFilter(mobileAuthenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers().frameOptions().disable();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private WebAuthenticationFilter getAuthenticationFilter() throws Exception {
        WebAuthenticationFilter webAuthenticationFilter = new WebAuthenticationFilter(authenticationManagerBean(authenticationConfiguration), accountRepository, accountHashRepository, devicesService, jwtUtil);
        webAuthenticationFilter.setFilterProcessesUrl("/api/web/login");
        webAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        webAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);
        return webAuthenticationFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Authorization", "Content-Type", "Accept", "Cache-Control"));
//        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-type"));

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
