package pl.edu.pw.security.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import pl.edu.pw.repository.AccountHashRepository;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.security.filter.AuthorizationFilter;
import pl.edu.pw.security.filter.MobileAuthenticationFilter;
import pl.edu.pw.security.filter.WebAuthenticationFilter;
import pl.edu.pw.service.devices.DevicesServiceImpl;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.service.otp.OtpService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private AuthenticationConfiguration authenticationConfiguration;
    private RestAuthenticationSuccessHandler successHandler;
    private RestAuthenticationFailureHandler failureHandler;
    private AccountRepository accountRepository;
    private AccountHashRepository accountHashRepository;
    private DevicesServiceImpl devicesService;
    private EmailSenderServiceImpl emailSenderService;
    private OtpService otpService;

    public WebSecurityConfig(AuthenticationConfiguration authenticationConfiguration, RestAuthenticationSuccessHandler successHandler, RestAuthenticationFailureHandler failureHandler, AccountRepository accountRepository, AccountHashRepository accountHashRepository, DevicesServiceImpl devicesService, EmailSenderServiceImpl emailSenderService, OtpService otpService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.accountRepository = accountRepository;
        this.accountHashRepository = accountHashRepository;
        this.devicesService = devicesService;
        this.emailSenderService = emailSenderService;
        this.otpService = otpService;
    }

    @Value("${jwt.expirationTime}")
    private long jwtExpirationTime;

    @Value("${refreshToken.expirationTime}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        desktop app processing
        WebAuthenticationFilter webAuthenticationFilter = getAuthenticationFilter();
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(accountRepository);

//        mobile app processing
        MobileAuthenticationFilter mobileAuthenticationFilter = new MobileAuthenticationFilter(authenticationManagerBean(authenticationConfiguration), accountRepository, accountHashRepository);
        mobileAuthenticationFilter.setFilterProcessesUrl("/api/mobile/login");


        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/api/web/login/**", "/api/mobile/login/**").permitAll()
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
        WebAuthenticationFilter webAuthenticationFilter = new WebAuthenticationFilter(
                                authenticationManagerBean(authenticationConfiguration),
                                accountRepository, accountHashRepository, devicesService, otpService, emailSenderService,
                                jwtExpirationTime, refreshTokenExpirationTime, jwtSecret);

        webAuthenticationFilter.setFilterProcessesUrl("/api/web/login");
//        webAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        webAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);
        return webAuthenticationFilter;
    }
}
