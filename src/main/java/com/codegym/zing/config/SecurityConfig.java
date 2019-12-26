package com.codegym.zing.config;

import com.codegym.zing.filter.JwtAuthenticationFilter;
import com.codegym.zing.service.UserService;
import com.codegym.zing.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN = "/login";

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    @Autowired
    private UserService userService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public RestAuthenticationEntryPoint restServicesEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/**");
        http.httpBasic().authenticationEntryPoint(restServicesEntryPoint());
        http.authorizeRequests()
                .antMatchers("/",
                        LOGIN,
                        "/register",
                        "/forgot-password").permitAll()
                .antMatchers(HttpMethod.GET,
                        "/songs/**",
                        "/albums/**",
                        "/singers/**"
                       ).permitAll()
                .antMatchers(HttpMethod.GET,
                        "/users/**",
                        "/userCurrent",
                        "/playlists/**",
                        "/new-password/**").access("hasAnyRole('ROLE_USER', 'ROLE_SINGER')")
                .antMatchers(HttpMethod.POST,
                             "/playlists/**",
                        "/new-password/**").access("hasAnyRole('ROLE_USER', 'ROLE_SINGER')")
                .antMatchers(HttpMethod.POST,
                        "/songs/**",
                        "/albums/**",
                        "/singers/**"
                ).access("hasRole('ROLE_SINGER')")
                .antMatchers(HttpMethod.PUT,
                        "/songs/**",
                        "/albums/**",
                        "/playlists/**",
                        "/singers/**"
                ).access("hasRole('ROLE_SINGER')")
                .antMatchers(HttpMethod.DELETE,
                        "/songs/**",
                        "/albums/**",
                        "/playlists/**",
                        "/singers/**"
                ).access("hasRole('ROLE_SINGER')")
                .anyRequest().authenticated()
                .and().csrf()
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());
        http.cors();
    }
}
