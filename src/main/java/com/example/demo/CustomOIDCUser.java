package com.example.demo;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public class CustomOIDCUser extends DefaultOidcUser implements MyUserInfo{
    private String email;
    private String name ;
    public CustomOIDCUser(DefaultOidcUser defaultOidcUser,String a_email,String a_name){
        super(defaultOidcUser.getAuthorities(),defaultOidcUser.getIdToken(),defaultOidcUser.getUserInfo());
        email = a_email;
        name = a_name;
    }
    public CustomOIDCUser(DefaultOidcUser defaultOidcUser){
        super(defaultOidcUser.getAuthorities(),defaultOidcUser.getIdToken(),defaultOidcUser.getUserInfo());
        email = defaultOidcUser.getAttribute("email");
        name =  defaultOidcUser.getAttribute("name");
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
