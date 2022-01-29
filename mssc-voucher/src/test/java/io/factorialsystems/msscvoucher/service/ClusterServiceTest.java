package io.factorialsystems.msscvoucher.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CommonsLog
@SpringBootTest
class ClusterServiceTest {

    @Autowired
    private ClusterService clusterService;

    @Test
    void findAll() {
//        PagedDto<ClusterDto> clusters = clusterService.findAll(1, 20);
//        assertNotNull(clusters);
//        assert(clusters.getTotalSize() > 1);
//        log.info(clusters);
    }

    @Test
    void findById() {
//        String id = "0d7d07b2-43a8-11ec-8b30-35fc519e26e2";
//
//        ClusterDto dto = clusterService.findById(id);
//        assertNotNull(dto);
//        assertEquals(id, dto.getId());
//        log.info(dto);
    }

    @Test
    void save() {
//        ClusterDto clusterDto = new ClusterDto();
//        clusterDto.setAmount(new BigDecimal(12345678.00));
//        clusterDto.setName("New Cluster");
//        clusterDto.setDescription("New Description");
//
//        clusterService.save("adebola", clusterDto);
    }

    @Test
    void update() {
//        String name = "updateName";
//        String id = "0d7d07b2-43a8-11ec-8b30-35fc519e26e2";
//
//        ClusterDto dto = clusterService.findById(id);
//        assertNotNull(dto);
//
//        dto.setName(name);
//        clusterService.update(dto.getId(), dto);
//
//        ClusterDto dto1 = clusterService.findById(id);
//        assertNotNull(dto);
//        assertEquals(dto1.getName(), name);
    }

    @Test
    void activateCluster() {
//        String id = "0d7d07b2-43a8-11ec-8b30-35fc519e26e2";
//        clusterService.activateCluster(id);
//        ClusterDto dto = clusterService.findById(id);
//
//        assertNotNull(dto);
//        assertEquals(Boolean.TRUE, dto.getActivated());
//        assertEquals("__debug", dto.getActivatedBy());
//        log.info(dto);
    }

    @Test
    void suspendCluster() {
//        String id = "0d7d07b2-43a8-11ec-8b30-35fc519e26e2";
//        clusterService.suspendCluster(id);
//
//        ClusterDto dto = clusterService.findById(id);
//        assertNotNull(dto);
//        assertEquals(true, dto.getSuspended());
    }

    @Test
    void unSuspendCluster() {
//        String id = "0d7d07b2-43a8-11ec-8b30-35fc519e26e2";
//        clusterService.unsuspendCluster(id);
//
//        ClusterDto dto = clusterService.findById(id);
//        assertNotNull(dto);
//        assertEquals(false, dto.getSuspended());
    }
}
