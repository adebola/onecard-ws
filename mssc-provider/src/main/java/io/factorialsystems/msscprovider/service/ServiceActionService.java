package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.web.mapper.action.ServiceActionMapstructMapper;
import io.factorialsystems.msscprovider.web.model.PagedDto;
import io.factorialsystems.msscprovider.web.model.ServiceActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private PagedDto<ServiceActionDto> createDto(Page<ServiceAction> actions) {
        PagedDto<ServiceActionDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) actions.getTotal());
        pagedDto.setPageNumber(actions.getPageNum());
        pagedDto.setPageSize(actions.getPageSize());
        pagedDto.setPages(actions.getPages());
        pagedDto.setList(serviceActionMapstructMapper.listActionToActionDto(actions.getResult()));
        return pagedDto;
    }

    public ServiceActionDto getProviderAction(Integer id) {
        return serviceActionMapstructMapper.actionToActionDto(serviceActionMapper.findById(id));
    }

    public Integer saveAction(String userName, ServiceActionDto dto) {
        ServiceAction action = serviceActionMapstructMapper.actionDtoToAction(dto);
        action.setId(null);
        action.setCreatedBy(userName);

        serviceActionMapper.save(action);
        if (action.getId() != null && action.getId() > 0) {
            return action.getId();
        }

        throw new RuntimeException("Error Saving Service Action");
    }

    public void updateAction(Integer id, ServiceActionDto dto) {
        dto.setId(id);
        serviceActionMapper.update(serviceActionMapstructMapper.actionDtoToAction(dto));
    }
}
