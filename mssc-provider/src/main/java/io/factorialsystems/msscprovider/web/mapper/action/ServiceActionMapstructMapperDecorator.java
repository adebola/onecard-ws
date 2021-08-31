package io.factorialsystems.msscprovider.web.mapper.action;


import io.factorialsystems.msscprovider.dao.ProviderMapper;
import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.web.model.ServiceActionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class ServiceActionMapstructMapperDecorator implements ServiceActionMapstructMapper {

    private ProviderMapper providerMapper;
    private ServiceActionMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(ServiceActionMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setProviderMapper(ProviderMapper providerMapper) {
        this.providerMapper = providerMapper;
    }

    @Override
    public ServiceAction actionDtoToAction(ServiceActionDto dto) {

        Provider provider = providerMapper.findByCode(dto.getProviderCode());

        if (provider == null) {
            throw new RuntimeException(String.format("Provider Code does not exist Code (%s)", dto.getProviderCode()));
        }

        ServiceAction action = mapstructMapper.actionDtoToAction(dto);
        action.setProviderCode(provider.getCode());
        action.setProviderName(provider.getName());
        action.setProviderId(provider.getId());

        return action;
    }

    @Override
    public ServiceActionDto actionToActionDto(ServiceAction action) {
        return mapstructMapper.actionToActionDto(action);
    }

    @Override
    public List<ServiceAction> listActionDtoToAction(List<ServiceActionDto> dtoList) {
        return mapstructMapper.listActionDtoToAction(dtoList);
    }

    @Override
    public List<ServiceActionDto> listActionToActionDto(List<ServiceAction> actions) {
        return mapstructMapper.listActionToActionDto(actions);
    }
}
