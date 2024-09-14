package com.v01.techgear_server.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.enums.AuthProvider;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;

@Service
public class CustomOidcUserService extends OidcUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        processOAuth2User(oidcUser, AuthProvider.GOOGLE);
        return oidcUser;
    }

    private void processOAuth2User(OidcUser oidcUser, AuthProvider google) {
        String email = oidcUser.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setProvider(google); // update the provider if necessary
        } else {
            user = new User();
            user.setUsername(oidcUser.getName());
            user.setEmail(email);
            user.setProvider(google);
        }
        userRepository.save(user);
    }

}
