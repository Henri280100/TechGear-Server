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

    public DataFetcher<User> dataFetcherUpdateUserName() {
        DataFetcher<User> retVal = new DataFetcher<User>() {

            @Override
            public User get(DataFetchingEnvironment environment) throws Exception {
                Long id = environment.getArgument("id");
                String newName = environment.getArgument("userName");

                User user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id " + id));

                Optional<User> existingUser = userRepository.findByUserName(newName);

                if (existingUser.isPresent() && !existingUser.get().getUserId().equals(id)) {
                    throw new RuntimeException("User name '" + newName + "' already exists, please try again.");
                }

                user.setUserName(newName);
                LOGGER.info("Updated user with id: {}", id);

                return userRepository.save(user);
            }

        };
        return retVal;
    }

    public DataFetcher<User> dataFetcherUpdateUserPassword() {
        DataFetcher<User> retVal = new DataFetcher<User>() {

            @Override
            public User get(DataFetchingEnvironment environment) throws Exception {
                Long id = environment.getArgument("id");
                String newPassword = environment.getArgument("password");

                User user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

                if (user.getPassword().equals(newPassword)) {

                }

                user.setPassword(newPassword);

                LOGGER.info("Successfully updated user password", user.getPassword());

                return userRepository.save(user);
            }

        };
        return retVal;
    }

    // public DataFetcher<User> dataFetcherUpdateUserEmail() {

    // }
}