package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SimpleOauth2Application {

	public static void main(String[] args) {
		SpringApplication.run(SimpleOauth2Application.class, args);
	}
	@Bean
	public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/login", "/error","/logout").permitAll()
						.anyRequest().authenticated()
				).csrf(c -> c.disable()) // allow logout with get request - Demo purpose
				.oauth2Login(oauth2 -> oauth2.loginPage("/login").userInfoEndpoint(userInfo -> userInfo
						.oidcUserService(this.oidcUserService()).userService(this.oauth2UserService()))).logout(l -> l.logoutSuccessUrl("/login"));
		return http.build();
	}

	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		final OidcUserService delegate = new OidcUserService();

		return (userRequest) -> {
			// Delegate to the default implementation for loading a user
			delegate.setAccessibleScopes( new HashSet<String>(
					Arrays.asList(OidcScopes.OPENID,OidcScopes.PROFILE, OidcScopes.EMAIL, OidcScopes.ADDRESS, OidcScopes.PHONE) ));
			DefaultOidcUser oidcUser = (DefaultOidcUser) delegate.loadUser(userRequest);
			CustomOIDCUser coidcUser = new CustomOIDCUser(oidcUser);

			OAuth2AccessToken accessToken = userRequest.getAccessToken();

			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			coidcUser.getAttributes().forEach((key, value) -> {
				System.out.println(key + " "+value);
			});

			System.out.println("userinfo.fullname()" + " "+coidcUser.getUserInfo().getFullName());
			System.out.println("userinfo.email()" + " "+coidcUser.getUserInfo().getEmail());
			System.out.println("coidcUser.getEmail()" + " "+coidcUser.getEmail());
			System.out.println("coidcUser.getName()" + " "+coidcUser.getName());
			System.out.println("userinfo.fullname()" + " "+coidcUser.getUserInfo().getFullName());


			return coidcUser;

		};
	}
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
		final OAuth2UserService delegate = new DefaultOAuth2UserService();

		return (userRequest) -> {

			// Get DefaultOAuth2User
			DefaultOAuth2User oAuth2User = (DefaultOAuth2User) delegate.loadUser(userRequest);
			String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
					.getUserNameAttributeName();
			CustomOauth2User customOauth2User = new CustomOauth2User(oAuth2User,userNameAttributeName);

			OAuth2AccessToken accessToken = userRequest.getAccessToken();

			customOauth2User.getAttributes().forEach((key, value) -> {
				System.out.println(key + " "+value);
			});

			System.out.println("coidcUser.getEmail()" + " "+customOauth2User.getEmail());
			System.out.println("coidcUser.getName()" + " "+customOauth2User.getName());


			return customOauth2User;
		};
	}

}

