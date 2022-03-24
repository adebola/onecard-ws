package io.factorialsystems.msscprovider.tasks;

import io.factorialsystems.msscprovider.dao.NewScheduledRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import io.factorialsystems.msscprovider.service.NewScheduledRechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
//    private final ScheduledRechargeMapper scheduledRechargeMapper;
//    private final ScheduledRechargeService scheduledRechargeService;

    private final NewScheduledRechargeMapper newScheduledRechargeMapper;
    private final NewScheduledRechargeService newScheduledRechargeService;

//    @Scheduled(initialDelay = 120000, fixedRateString = "${scheduled.rate}")
//    public void runScheduledRecharge() {
//
//        log.info("Periodical Recharge Requests Run................");
//
//        List<ScheduledRechargeRequest> requests = scheduledRechargeMapper.findOpenRequests();
//
//        if (requests != null && requests.size() > 0) {
//            log.info("Loaded Scheduled Recharge Requests {}", requests.size());
//            scheduledRechargeService.runRecharges(requests);
//        }
//    }

    @Scheduled(initialDelay = 120000, fixedRateString = "${scheduled.rate}")
    public void runScheduledRecharge() {

        log.info("Running Scheduled Recharge Requests Run................");

        List<NewScheduledRechargeRequest> requests = newScheduledRechargeMapper.findOpenRequests();

        if (requests != null && requests.size() > 0) {
            log.info("Loaded Scheduled Recharge Requests for Processing {}", requests.size());
            newScheduledRechargeService.runRecharges(requests);
        }
    }
}
