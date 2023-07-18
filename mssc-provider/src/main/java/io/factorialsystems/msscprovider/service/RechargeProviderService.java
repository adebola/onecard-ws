package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.RechargeProviderMapper;
import io.factorialsystems.msscprovider.domain.RechargeProvider;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.provider.RechargeProviderDto;
import io.factorialsystems.msscprovider.dto.provider.RechargeProviderExDto;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeProviderMapstructMapper;
import io.factorialsystems.msscprovider.recharge.Balance;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<RechargeProviderDto> findAllWithBalances() {
        PageHelper.startPage(1, 1000);
        try (Page<RechargeProvider> providers = rechargeMapper.findAll()) {

            return providers.getResult()
                    .stream()
                    .map(r -> {
                        RechargeProviderDto rechargeProviderDto = rechargeMapstructMapper.rechargeToRechargeDto(r);

                        AbstractFactory abstractFactory = factoryProducer.getFactory(r.getCode());

                        if (abstractFactory != null) {
                            try {
                                Balance balance = abstractFactory.getBalance();

                                if (balance != null) {
                                    rechargeProviderDto.setBalance(balance.getBalance());
                                }
                            } catch (Exception e) {
                                log.error("Error getting balance for {}", rechargeProviderDto.getCode());
                                log.error("Error Message {}", e.getMessage());
                            }
                        }

                        return rechargeProviderDto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("FindAllWithBalance Error {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
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

    public List<RechargeProviderExDto> findByServiceId(Integer id) {
        return rechargeMapstructMapper.listRechargeProviderExToRechargeProviderExDto(rechargeMapper.findByServiceId(id));
    }

    public RechargeProviderDto save(RechargeProviderDto dto) {
        RechargeProvider rechargeProvider = rechargeMapstructMapper.rechargeDtoToRecharge(dto);
        rechargeProvider.setCreatedBy(ProviderSecurity.getUserName());
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
