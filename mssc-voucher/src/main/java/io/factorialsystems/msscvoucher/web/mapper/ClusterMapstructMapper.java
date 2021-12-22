package io.factorialsystems.msscvoucher.web.mapper;

import io.factorialsystems.msscvoucher.domain.Cluster;
import io.factorialsystems.msscvoucher.web.model.ClusterDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = DateMapper.class)
public interface ClusterMapstructMapper {
    ClusterDto clusterToClusterDto(Cluster cluster);
    Cluster clusterDtoToCluster(ClusterDto clusterDto);
    List<ClusterDto> listClusterToClusterDto(List<Cluster> clusters);
    List<Cluster> listClusterDtoToCluster(List<ClusterDto> clusterDtos);
}
