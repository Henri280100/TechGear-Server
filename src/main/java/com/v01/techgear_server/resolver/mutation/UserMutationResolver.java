package com.v01.techgear_server.resolver.mutation;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class UserMutationResolver {

    @Autowired
    private UserRepository userRepository;

    private static Logger LOGGER = LoggerFactory.getLogger(UserMutationResolver.class);

    public DataFetcher<User> dataFetcherUpdateUsername() {
        DataFetcher<User> retUsernameUpdateVal = new DataFetcher<User>() {

            @Override
            public User get(DataFetchingEnvironment environment) throws Exception {
                Long id = environment.getArgument("id");
                String newName = environment.getArgument("username");

                User user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id " + id));

                Optional<User> existingUser = userRepository.findByUsername(newName);

                if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                    throw new RuntimeException("User name '" + newName + "' already exists, please try again.");
                }

                user.setUsername(newName);
                LOGGER.info("Updated user with id: {}", id);

                return userRepository.save(user);
            }

        };
        return retUsernameUpdateVal;
    }

    
    // public DataFetcher<User> dataFetcherUpdateUserEmail() {
    //     DataFetcher<User> retUserEmailUpdateVal = new DataFetcher<User>() {

    //         @Override
    //         public User get(DataFetchingEnvironment environment) throws Exception {
    //             Long id = environment.getArgument("id");
    //             String newEmail = environment.getArgument("email");
    //         }};
    //     return retUserEmailUpdateVal;
    // }
}