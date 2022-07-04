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
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.security.filter.AuthenticationFilter;
import pl.edu.pw.security.filter.AuthorizationFilter;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.service.otp.OtpService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig {

    private AuthenticationConfiguration authenticationConfiguration;
    private AccountRepository accountRepository;
    private EmailSenderServiceImpl emailSenderService;
    private OtpService otpService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManagerBean(authenticationConfiguration),emailSenderService,otpService);
        authenticationFilter.setFilterProcessesUrl("/api/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(accountRepository);

        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**").permitAll()
                .and()
                .authorizeRequests().antMatchers("/api/account/verify").permitAll()
                .and()
                .authorizeRequests().antMatchers("/api/registration/**").permitAll()
                .and()
                .authorizeRequests().antMatchers("/api/password/**").permitAll()
                .and()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();

        http.addFilter(authenticationFilter);
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    




}
