package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.RechargeProviderMapper;
import io.factorialsystems.msscprovider.domain.RechargeProvider;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.RechargeProviderDto;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeProviderMapstructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeProviderService {
    private final RechargeProviderMapper rechargeMapper;
    private final RechargeProviderMapstructMapper rechargeMapstructMapper;

    public PagedDto<RechargeProviderDto> findAll(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<RechargeProvider> providers = rechargeMapper.findAll();
        return createDto(providers);
    }

    public RechargeProviderDto findById(Integer id) {
        return rechargeMapstructMapper.rechargeToRechargeDto(rechargeMapper.findById(id));
    }

    public List<RechargeProviderDto> findByServiceId(Integer id) {
        return rechargeMapstructMapper.listRechargeToRechargeDto(rechargeMapper.findByServiceId(id));
    }

    public RechargeProviderDto save(RechargeProviderDto dto) {
        RechargeProvider rechargeProvider = rechargeMapstructMapper.rechargeDtoToRecharge(dto);
        rechargeMapper.save(rechargeProvider);

        if (rechargeProvider.getId() == null) {
            throw new RuntimeException("Error saving Recharge Provider");
        }

        return rechargeMapstructMapper.rechargeToRechargeDto(rechargeMapper.findById(rechargeProvider.getId()));
    }

    public RechargeProviderDto update(Integer id, RechargeProviderDto dto) {
        RechargeProvider rechargeProvider = rechargeMapstructMapper.rechargeDtoToRecharge(dto);
        rechargeMapper.update(rechargeProvider);
        return rechargeMapstructMapper.rechargeToRechargeDto(rechargeMapper.findById(rechargeProvider.getId()));
    }

    private PagedDto<RechargeProviderDto> createDto(Page<RechargeProvider> providers) {
        PagedDto<RechargeProviderDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) providers.getTotal());
        pagedDto.setPageNumber(providers.getPageNum());
        pagedDto.setPageSize(providers.getPageSize());
        pagedDto.setPages(providers.getPages());
        pagedDto.setList(rechargeMapstructMapper.listRechargeToRechargeDto(providers.getResult()));
        return pagedDto;
    }
}
