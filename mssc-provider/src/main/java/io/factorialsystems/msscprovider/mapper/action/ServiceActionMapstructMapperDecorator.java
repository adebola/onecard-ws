package io.factorialsystems.msscprovider.mapper.action;


import io.factorialsystems.msscprovider.dao.ProviderMapper;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.Action;
import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.dto.ServiceActionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ServiceActionMapstructMapperDecorator implements ServiceActionMapstructMapper {

    private ProviderMapper providerMapper;
    private ServiceActionMapper serviceActionMapper;
    private ServiceActionMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(ServiceActionMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setProviderMapper(ProviderMapper providerMapper) {
        this.providerMapper = providerMapper;
    }

    @Autowired
    public void setServiceActionMapper(ServiceActionMapper serviceActionMapper) {
        this.serviceActionMapper = serviceActionMapper;
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

        List<Action> actions = serviceActionMapper.findAllActions();
        Optional<Action> optionalAction = actions.stream()
                .filter(a -> a.getAction().equals(dto.getActionName()))
                .findFirst();

        if (optionalAction.isEmpty()) {
            throw new RuntimeException(String.format("Invalid Service Action (%s) please consult documentation", dto.getActionName()));
        }

        Action verifiedAction = optionalAction.get();
        action.setActionId(verifiedAction.getId());
        action.setActionName(verifiedAction.getAction());

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
