package io.factorialsystems.msscvoucher.web.mapper;

import io.factorialsystems.msscvoucher.domain.Batch;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = DateMapper.class)
public interface BatchMapstructMapper {

    @Mapping(target = "createdDate", source = "createdAt")
    BatchDto batchToBatchDto(Batch batch);

    List<BatchDto> listBatchToBatchDto(List<Batch> batches);
}
