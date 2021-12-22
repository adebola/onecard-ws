package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscvoucher.dao.ClusterMapper;
import io.factorialsystems.msscvoucher.domain.Cluster;
import io.factorialsystems.msscvoucher.utils.K;
import io.factorialsystems.msscvoucher.web.mapper.ClusterMapstructMapper;
import io.factorialsystems.msscvoucher.web.model.ClusterDto;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.abs;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterService {
    private final AuditService auditService;
    private final ClusterMapper clusterMapper;
    private final ClusterMapstructMapper clusterMapstructMapper;

    private static final String CLUSTER_CREATED = "Cluster Created";
    private static final String CLUSTER_UPDATED = "Cluster Updated";
    private static final String CLUSTER_ACTIVATED = "Cluster Activated";
    private static final String CLUSTER_SUSPENDED = "Cluster Suspended";
    private static final String CLUSTER_UNSUSPENDED = "Cluster Unsuspended";

    public PagedDto<ClusterDto> findAll(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(clusterMapper.findAll());
    }

    public PagedDto<ClusterDto> search(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(clusterMapper.Search(searchString));
    }

    public List<ClusterDto> findValid() {
        return clusterMapstructMapper.listClusterToClusterDto(clusterMapper.findValid());
    }

    public ClusterDto findById(String id) {
        return clusterMapstructMapper.clusterToClusterDto(clusterMapper.findById(id));
    }

    public String save(String userName, ClusterDto clusterDto) {
        String id = UUID.randomUUID().toString();
        clusterDto.setCreatedBy(userName);
        clusterDto.setId(id);
        Cluster cluster = clusterMapstructMapper.clusterDtoToCluster(clusterDto);
        clusterMapper.save(cluster);

        final String message = String.format("Cluster Generated Name (%s) Amount (%.2f)", cluster.getName(), cluster.getAmount());
        auditService.auditEvent(message, CLUSTER_CREATED);

        return id;
    }

    public void update(String id, ClusterDto clusterDto) {
        Cluster cluster = clusterMapper.findById(id);

        if (cluster != null && clusterDto != null) {

            boolean changed = false;

            // Are we updating the Cluster Name
            if (!cluster.getName().equals(clusterDto.getName())) {
                cluster.setName(clusterDto.getName());
                changed = true;
            }

            // Are we updating the Description
            if (!cluster.getDescription().equals(clusterDto.getDescription())) {
                cluster.setDescription(clusterDto.getDescription());
                changed = true;
            }

            // Are we updating the Cluster Amount
            if (clusterDto.getAmount() != null && (abs(clusterDto.getAmount().doubleValue() - cluster.getAmount().doubleValue()) > K.epsilon) ) {
                if (cluster.getBalance().doubleValue() >  clusterDto.getAmount().doubleValue()) {
                    throw new RuntimeException("When adjusting a Cluster's Budget, the value must be greater than the existing balance");
                }
                changed = true;
                cluster.setAmount(clusterDto.getAmount());
            }

            if (changed) {
                clusterMapper.update(cluster);
            }
        }

        final String message = String.format("Cluster updated %s Updated", cluster.getId());
        auditService.auditEvent(message, CLUSTER_UPDATED);
    }

    public ClusterDto activateCluster(String id) {

        Cluster cluster = clusterMapper.findById(id);
        if (cluster != null && !cluster.getActivated()) {
            cluster.setActivated(true);
            cluster.setActivationDate(new Timestamp(new Date().getTime()));
            cluster.setActivatedBy(K.getUserName());
            clusterMapper.update(cluster);

            final String message = String.format("Cluster %s Activated", cluster.getId());
            auditService.auditEvent(message, CLUSTER_ACTIVATED);

            return clusterMapstructMapper.clusterToClusterDto(cluster);
        }

        return null;
    }

    public ClusterDto suspendCluster(String id) {
        Cluster cluster = clusterMapper.findById(id);

        if (cluster != null) {
            clusterMapper.suspend(id);
            cluster.setSuspended(true);

            final String message = String.format("Cluster %s Suspended", cluster.getId());
            auditService.auditEvent(message, CLUSTER_SUSPENDED);

            return clusterMapstructMapper.clusterToClusterDto(cluster);
        }

        return null;
    }

    public ClusterDto unsuspendCluster(String id) {
        Cluster cluster = clusterMapper.findById(id);

        if (cluster != null) {
            clusterMapper.unsuspend(id);
            cluster.setSuspended(false);

            final String message = String.format("Cluster %s Suspended", cluster.getId());
            auditService.auditEvent(message, CLUSTER_UNSUSPENDED);

            return clusterMapstructMapper.clusterToClusterDto(cluster);
        }

        return null;
    }

    private PagedDto<ClusterDto> createDto(Page<Cluster> clusters) {
        PagedDto<ClusterDto> pagedDto = new PagedDto<>();
        pagedDto.setPages(clusters.getPages());
        pagedDto.setTotalSize((int) clusters.getTotal());
        pagedDto.setPageNumber(clusters.getPageNum());
        pagedDto.setPageSize(clusters.getPageSize());
        pagedDto.setList(clusterMapstructMapper.listClusterToClusterDto(clusters.getResult()));

        return pagedDto;
    }
}
