package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.RechargeProviderMapper;
import io.factorialsystems.msscprovider.domain.RechargeProvider;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.RechargeProviderDto;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeProviderMapstructMapper;
import io.factorialsystems.msscprovider.recharge.Balance;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeProviderService {
    private final FactoryProducer factoryProducer;
    private final RechargeProviderMapper rechargeMapper;
    private final RechargeProviderMapstructMapper rechargeMapstructMapper;

    public PagedDto<RechargeProviderDto> findAll(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<RechargeProvider> providers = rechargeMapper.findAll();
        return createDto(providers);
    }

    public RechargeProviderDto findById(Integer id) {
        RechargeProviderDto dto = rechargeMapstructMapper.rechargeToRechargeDto(rechargeMapper.findById(id));
        dto.setBalance(new BigDecimal(0));

        AbstractFactory abstractFactory = factoryProducer.getFactory(dto.getCode());

        if (abstractFactory != null) {
            Balance balance = abstractFactory.getBalance();

            if (balance != null) {
                dto.setBalance(balance.getBalance());
            }
        }

        return dto;
    }

    public List<RechargeProviderDto> findByServiceId(Integer id) {
        return rechargeMapstructMapper.listRechargeToRechargeDto(rechargeMapper.findByServiceId(id));
    }

    public RechargeProviderDto save(RechargeProviderDto dto) {
        RechargeProvider rechargeProvider = rechargeMapstructMapper.rechargeDtoToRecharge(dto);
        rechargeProvider.setCreatedBy(K.getUserName());
        rechargeMapper.save(rechargeProvider);

        return rechargeMapstructMapper.rechargeToRechargeDto(rechargeMapper.findById(rechargeProvider.getId()));
    }

    public RechargeProviderDto update(Integer id, RechargeProviderDto dto) {
        RechargeProvider rechargeProvider = rechargeMapstructMapper.rechargeDtoToRecharge(dto);
        rechargeProvider.setId(id);
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
