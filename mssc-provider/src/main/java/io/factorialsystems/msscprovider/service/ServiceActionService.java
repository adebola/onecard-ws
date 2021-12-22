package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.Action;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.ServiceActionDto;
import io.factorialsystems.msscprovider.mapper.action.ServiceActionMapstructMapper;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceActionService {

    private final ServiceActionMapper serviceActionMapper;
    private final ServiceActionMapstructMapper serviceActionMapstructMapper;

    public PagedDto<ServiceActionDto> getProviderActions(String code, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<ServiceAction> actionsPage = serviceActionMapper.findByProviderCode(code);

        return createDto(actionsPage);
    }

    public ServiceActionDto getProviderAction(Integer id) {
        return serviceActionMapstructMapper.actionToActionDto(serviceActionMapper.findById(id));
    }

    public ServiceActionDto saveAction(String userName, ServiceActionDto dto) {

        ServiceAction action = serviceActionMapstructMapper.actionDtoToAction(dto);
        action.setId(null);
        action.setCreatedBy(userName);

        serviceActionMapper.save(action);

        if (action.getId() != null && action.getId() > 0) {
            return serviceActionMapstructMapper.actionToActionDto(serviceActionMapper.findById(action.getId()));
        }

        throw new RuntimeException("Error Saving Service Action");
    }

    public void updateAction(Integer id, ServiceActionDto dto) {
        dto.setId(id);
        serviceActionMapper.update(serviceActionMapstructMapper.actionDtoToAction(dto));
    }

    public ServiceActionDto activateService(Integer id) {
        ServiceAction action = serviceActionMapper.findById(id);

        if (action != null) {
            action.setActivated(true);
            action.setActivationDate(new Timestamp(System.currentTimeMillis()));
            action.setActivatedBy(K.getUserName());

            serviceActionMapper.update(action);

            return serviceActionMapstructMapper.actionToActionDto(action);
        }

        return null;
    }

    public ServiceActionDto suspendService(Integer id) {
        ServiceAction action = serviceActionMapper.findById(id);

        if (action != null) {
            action.setSuspended(true);
            serviceActionMapper.update(action);

            return serviceActionMapstructMapper.actionToActionDto(action);
        }

        return null;

    }

    public ServiceActionDto unsuspendService(Integer id) {
        ServiceAction action = serviceActionMapper.findById(id);

        if (action != null) {
            action.setSuspended(false);
            serviceActionMapper.update(action);

            return serviceActionMapstructMapper.actionToActionDto(action);
        }

        return null;
    }

    public void removeRechargeProviderFromService(Integer rechargeId, Integer serviceId) {
        Map<String, Integer> map = new HashMap<>();
        map.put("recharge_id", rechargeId);
        map.put("service_id", serviceId);
        serviceActionMapper.removeRechargeProvider(map);
    }

    public void addRechargeProviderToService(Integer rechargeId, Integer serviceId) {
        Map<String, Integer> map = new HashMap<>();
        map.put("recharge_id", rechargeId);
        map.put("service_id", serviceId);
        serviceActionMapper.addRechargeProvider(map);
    }

    public List<Action> getActions() {
        return serviceActionMapper.findAllActions();
    }

    private PagedDto<ServiceActionDto> createDto(Page<ServiceAction> actions) {
        PagedDto<ServiceActionDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) actions.getTotal());
        pagedDto.setPageNumber(actions.getPageNum());
        pagedDto.setPageSize(actions.getPageSize());
        pagedDto.setPages(actions.getPages());
        pagedDto.setList(serviceActionMapstructMapper.listActionToActionDto(actions.getResult()));
        return pagedDto;
    }
}
