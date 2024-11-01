package com.v01.techgear_server.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.enums.AuthProvider;
import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.exception.UserRolesNotFoundException;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.RoleRepository;
import com.v01.techgear_server.repo.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            // Handle Google user (OidcUser is a subtype of OAuth2User)
            processOAuth2User((OidcUser) oAuth2User, AuthProvider.GOOGLE);
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            // Handle Facebook user
            processOAuth2User(oAuth2User, AuthProvider.FACEBOOK);
        } else {
            // Handle unknown providers or throw an exception
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_provider"), "Invalid provider type");
        }
        return oAuth2User;
    }

    private void processOAuth2User(OAuth2User oAuth2User, AuthProvider provider) {
        String email = oAuth2User.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setProvider(provider); // update the provider if necessary
        } else {
            user = new User();
            user.setUsername(oAuth2User.getAttribute("name"));
            user.setEmail(email);
            user.setProvider(provider);
            user.setPassword(null);
            Role userRole = roleRepository.findByRoleType(Roles.ROLE_USER)
                    .orElseThrow(() -> new UserRolesNotFoundException("Error: Role is not found"));
            user.setRoles(Collections.singleton(userRole));
        }

        userRepository.save(user);
    }

}
