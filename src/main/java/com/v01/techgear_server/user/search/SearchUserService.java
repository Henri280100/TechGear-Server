package com.v01.techgear_server.user.search;

import com.v01.techgear_server.user.dto.UserSearchRequest;
import com.v01.techgear_server.user.dto.UserSearchResponse;
import org.typesense.model.SearchResult;

import java.util.concurrent.CompletableFuture;

public interface SearchUserService {
	CompletableFuture<UserSearchResponse> searchUser(UserSearchRequest request);

	UserSearchResponse buildSearchResponse(SearchResult searchResult);
}
