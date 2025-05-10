package com.v01.techgear_server.shared.mapping;

import java.util.Collections;
import java.util.List;

import org.mapstruct.*;

import com.v01.techgear_server.shared.dto.MediaDTO;
import com.v01.techgear_server.shared.model.Media;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", uses = {})
public interface MediaMapper extends BaseMapper<Media, MediaDTO> {

	@Override
	@Mapping(target = "productDetails.detailsId", ignore = true)
	@Mapping(target = "filename", source = "mediaFilename")
	@Mapping(target = "dimensions", ignore = true)
	MediaDTO toDTO(Media media);

	@Override
	@Mapping(target = "uploadedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "productDetail", ignore = true)
	@Mapping(target = "mediaFilename", source = "filename")
	@Mapping(target = "mediaContentType", ignore = true)
	@Mapping(target = "lastModifiedAt", ignore = true)
	@Mapping(target = "fileSize", ignore = true)
	@Mapping(target = "data", ignore = true)
	@Mapping(target = "width", ignore = true)
	@Mapping(target = "height", ignore = true)
	Media toEntity(MediaDTO mediaDTO);

	@Override
	default List<MediaDTO> toDTOList(List<Media> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
	}

	@Override
	default List<Media> toEntityList(List<MediaDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
	}


}
