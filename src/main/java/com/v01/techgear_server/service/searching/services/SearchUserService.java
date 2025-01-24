package com.v01.techgear_server.service.searching.services;

import com.v01.techgear_server.dto.UserSearchRequest;
import com.v01.techgear_server.dto.UserSearchResponse;
import org.typesense.model.SearchResult;

import java.util.concurrent.CompletableFuture;

public interface SearchUserService {
	CompletableFuture<UserSearchResponse> searchUser(UserSearchRequest request);

	UserSearchResponse buildSearchResponse(SearchResult searchResult);
}
