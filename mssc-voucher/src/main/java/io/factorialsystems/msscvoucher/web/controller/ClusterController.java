package io.factorialsystems.msscvoucher.web.controller;

import io.factorialsystems.msscvoucher.dto.out.MessageDto;
import io.factorialsystems.msscvoucher.service.BatchService;
import io.factorialsystems.msscvoucher.service.ClusterService;
import io.factorialsystems.msscvoucher.utils.K;
import io.factorialsystems.msscvoucher.web.model.ClusterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_NUMBER;
import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_SIZE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cluster")
public class ClusterController {
    private final BatchService batchService;
    private final ClusterService clusterService;

    @GetMapping
    public ResponseEntity<?> getClusters(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(clusterService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchClusters(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                            @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(clusterService.search(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @GetMapping("/valid")
    public ResponseEntity<?> validClusters() {
        return new ResponseEntity<>(clusterService.findValid(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClusterDto> getClusterById(@PathVariable("id") String id) {
        return new ResponseEntity<>(clusterService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MessageDto> createCluster(@Valid @RequestBody ClusterDto clusterDto) {
        String newId = clusterService.save(K.getUserName(), clusterDto);
        return new ResponseEntity<>(new MessageDto(newId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCluster(@PathVariable("id") String id, @Valid @RequestBody ClusterDto clusterDto) {
        try {
            clusterService.update(id, clusterDto);
            return new ResponseEntity<>(new MessageDto("Cluster updated"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/activate/{id}")
    public ResponseEntity<?> activateCluster(@PathVariable("id") String id) {

        try {
            ClusterDto dto = clusterService.activateCluster(id);

            if (dto == null) {
                return new ResponseEntity<>(new MessageDto(String.format("Unable to locate Cluster %s", id)), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/suspend/{id}")
    public ResponseEntity<? extends Object> suspendCluster(@PathVariable("id") String id) {
        try {
            ClusterDto dto = clusterService.suspendCluster(id);

            if (dto == null) {
                return new ResponseEntity<>(new MessageDto(String.format("Unable to locate Cluster %s", id)), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/unsuspend/{id}")
    public ResponseEntity<?> unSuspendCluster(@PathVariable("id") String id) {
        try {
            ClusterDto dto = clusterService.unsuspendCluster(id);

            if (dto == null) {
                return new ResponseEntity<>(new MessageDto(String.format("Unable to locate Cluster %s", id)), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
