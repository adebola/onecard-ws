package io.factorialsystems.msscvoucher.web.mapper;


import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.dto.internal.VoucherChangeRequestInternal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
public interface VoucherChangeRequestMapper {

    @Mappings({
            @Mapping(source = "expiryDate", target = "expiryDate"),
            @Mapping(source = "denomination", target = "denomination")
    })
    VoucherChangeRequest fromInternal(VoucherChangeRequestInternal internal);

    @Mappings({
            @Mapping(source = "expiryDate", target = "expiryDate"),
            @Mapping(source = "denomination", target = "denomination")
    })
    VoucherChangeRequestInternal toInternal(VoucherChangeRequest request);

    List<VoucherChangeRequest> fromInternalList(List<VoucherChangeRequestInternal> internalList);
    List<VoucherChangeRequestInternal> toInternalList(List<VoucherChangeRequest> requestList);
}
