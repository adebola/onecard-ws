package io.factorialsystems.msscprovider.config;

import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.recharge.onecard.OnecardDataRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoMobileDataRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoSmileRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoSpectranetRecharge;
import io.factorialsystems.msscprovider.recharge.smile.SmileDataRecharge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheProxy {
    public List<DataPlanDto> getRingoMobileDataPlans(String requestCode) {
        return ApplicationContextProvider.getBean(RingoMobileDataRecharge.class).getDataPlans(requestCode);
    }

    public List<DataPlanDto> getRingoSpectranetDataPlans(String requestCode) {
        return ApplicationContextProvider.getBean(RingoSpectranetRecharge.class).getDataPlans(requestCode);
    }

    public List<DataPlanDto> getRingoSmileDataPlans(String requestCode) {
        return ApplicationContextProvider.getBean(RingoSmileRecharge.class).getDataPlans(requestCode);
    }

    public List<DataPlanDto> getSmileDataPlans(String requestCode) {
        return ApplicationContextProvider.getBean(SmileDataRecharge.class).getDataPlans(requestCode);
    }

    public List<DataPlanDto> getOnecardPlans(String requestCode) {
        return ApplicationContextProvider.getBean(OnecardDataRecharge.class).getDataPlans(requestCode);
    }
}
