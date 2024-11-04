package com.ashuboi.photoappapiusers.photoappapiusers.Users.Security;

import com.ashuboi.photoappapiusers.photoappapiusers.Users.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;


@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final Environment environment;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment environment, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.environment = environment;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //This will make springframework call this method at the time of application startup, our methods will be
    // executed and the http security object that we configure in this method will be placed in application context
    // once this object is inside the Spring application Context, Spring Framework will be abe to use it whenever it needs to
    // eg: we send a ship request to one of our API endpoints, Spring framework will take this request through a chain
    // of http filters, and one of these filters will validate request against security config that we create in this method
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        String gatewayIp = environment.getProperty("gateway.ip");

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(PathRequest.toH2Console(),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/users")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/users/**"))
                        .access(ipAddressAuthorizationManager(gatewayIp))
                        .anyRequest().authenticated());
        http.authenticationManager(authenticationManager);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));


        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager);
        http.addFilter(authenticationFilter);
        return http.build();
    }

    // Custom AuthorizationManager to restrict access by IP address
    private AuthorizationManager<RequestAuthorizationContext> ipAddressAuthorizationManager(String ipAddress) {
        return (authentication, context) -> {
            IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ipAddress);
            boolean allowed = ipAddressMatcher.matches(context.getRequest());
            return new AuthorizationDecision(allowed);
        };
    }
}
