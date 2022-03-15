package com.example.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 
 * @author hondayuki
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService memberDetailsService;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
		.antMatchers("/css/**"
					,"/img_pizza/**"
					,"/js/**"
					,"/fonts/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//ログイン画面と新規登録画面を許可
		http.authorizeRequests()
		.antMatchers("/order/confirm", "/order/histroy").authenticated()
		.anyRequest().permitAll();
		
		//ログイン設定
		http.formLogin()
			.loginPage("/")
			.loginProcessingUrl("/login")
			.failureUrl("/?error=true")
			.defaultSuccessUrl("/item/showList", false)
			.usernameParameter("email")
			.passwordParameter("password");
		
		//ログアウト設定
		http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout**"))
			.logoutSuccessUrl("/")
			.deleteCookies("JSESSIONID")
			.invalidateHttpSession(true);
	}
	
	/**
	 * 認証設定
	 * 
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(memberDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}
	
	/**
	 * エンコーダー設定
	 * 
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
