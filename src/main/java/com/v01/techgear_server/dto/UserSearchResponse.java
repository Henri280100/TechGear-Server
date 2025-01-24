package com.v01.techgear_server.dto;

import com.v01.techgear_server.model.FacetCount;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserSearchResponse {
	private List<UserDTO> users;
	private long totalResult;
	private Integer page;
	private Integer perPage;
	private Map<String, List<FacetCount>> facets;
}
