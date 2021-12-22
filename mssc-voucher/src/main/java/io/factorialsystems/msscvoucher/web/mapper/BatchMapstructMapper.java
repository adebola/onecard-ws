package io.factorialsystems.msscvoucher.web.mapper;

import io.factorialsystems.msscvoucher.domain.Batch;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = DateMapper.class)
public interface BatchMapstructMapper {
    BatchDto batchToBatchDto(Batch batch);
    Batch batchDtoToBatch(BatchDto batchDto);
    List<BatchDto> listBatchToBatchDto(List<Batch> batches);
    List<Batch> listBatchDtoToBatch(List<BatchDto> batchDtos);
}
