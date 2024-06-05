package sb.steradian.sbsteradian.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sb.steradian.sbsteradian.filter.JwtTokenFilter;
import sb.steradian.sbsteradian.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private AuthEntryPoint unauthorizedHandler;

	@Autowired
	private UserDetailsServiceImpl jwtUserDetailsService;
	
	@Autowired
	private JwtTokenFilter jwtTokenFilter;


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		try{
			auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception{
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception{
		httpSecurity.cors().and().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests().antMatchers("/sbsteradian/api/auth/**","/swagger-ui/**","/swagger-ui.html","/webjars/**","/v2/**","/swagger-resources/**").
            permitAll()
			.antMatchers("/sbsteradian/test/v1/**").permitAll()
			.anyRequest().authenticated();

        httpSecurity.addFilterBefore(jwtTokenFilter,UsernamePasswordAuthenticationFilter.class);
	}

//	@Bean
//	public Docket dosenApi() {
//		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("sbsamir.controller")).build();
//	}
}
