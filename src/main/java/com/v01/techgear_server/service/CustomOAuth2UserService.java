package com.v01.techgear_server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.enums.AuthProvider;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        processOAuth2User(oAuth2User, AuthProvider.FACEBOOK);
        
        return oAuth2User;
    }

    private void processOAuth2User(OAuth2User oAuth2User, AuthProvider facebook) {
        String email = oAuth2User.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setProvider(facebook); // update the provider if necessary
        } else {
            user = new User();
            user.setUsername(oAuth2User.getAttribute("name"));
            user.setEmail(email);
            user.setProvider(facebook);
        }
        userRepository.save(user);
    }
    
}

