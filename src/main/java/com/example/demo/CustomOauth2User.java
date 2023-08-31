package com.example.demo;



import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


public class CustomOauth2User extends DefaultOAuth2User implements MyUserInfo{
    private String email;
    private String name ;
    public CustomOauth2User( DefaultOAuth2User defaultOAuth2User, String a_nameAttributeKey){
        super(defaultOAuth2User.getAuthorities(),defaultOAuth2User.getAttributes(),a_nameAttributeKey);
        email = defaultOAuth2User.getAttribute("email");
        name = defaultOAuth2User.getAttribute("name");

    }

    @Override
    public String getEmail(){
        return email;
    }
    @Override
    public String getFullName(){
        return name;
    }

}
