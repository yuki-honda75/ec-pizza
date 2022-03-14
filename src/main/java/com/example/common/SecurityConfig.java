package com.example.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 
 * @author hondayuki
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
		.antMatchers("/css/**"
					,"/img/**"
					,"/js/**"
					,"/fonts/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//ログイン画面と新規登録画面を許可
		http.authorizeRequests()
			.antMatchers("/", "/toRegister", "/register").permitAll()
			.anyRequest().authenticated();
	}
}