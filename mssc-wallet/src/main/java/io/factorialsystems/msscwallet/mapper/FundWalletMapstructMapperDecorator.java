package io.factorialsystems.msscwallet.mapper;

import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.dto.FundWalletRequestDto;
import io.factorialsystems.msscwallet.dto.FundWalletResponseDto;
import io.factorialsystems.msscwallet.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FundWalletMapstructMapperDecorator implements FundWalletMapstructMapper {

    private FundWalletMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper( FundWalletMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Override
    public FundWalletRequest dtoToWalletRequest(FundWalletRequestDto dto) {
        return mapstructMapper.dtoToWalletRequest(dto);
    }

    @Override
    public FundWalletResponseDto requestToResponseDto(FundWalletRequest request) {
        return mapstructMapper.requestToResponseDto(request);
    }

    @Override
    public FundWalletRequestDto requestToRequestDto(FundWalletRequest request) {

        FundWalletRequestDto dto = mapstructMapper.requestToRequestDto(request);

        log.info(String.format("FundWalletRequestType %d", request.getFundType()));

        switch (request.getFundType()) {
            case AccountService.WALLET_SELF_FUNDED:
                dto.setType(AccountService.WALLET_SELF_FUNDED_STRING);
                break;

            case AccountService.WALLET_ONECARD_FUNDED:
                dto.setType(AccountService.WALLET_ONECARD_FUNDED_STRING);
                break;

            case AccountService.WALLET_USER_FUNDED:
                dto.setType(AccountService.WALLET_USER_FUNDED_STRING);
                break;

            case AccountService.WALLET_USER_DEBITED:
                dto.setType(AccountService.WALLET_USER_DEBIT_STRING);
                break;

            default:
                throw new RuntimeException(String.format("Invalid Fund Wallet Request Type %d unable to convert", request.getFundType()));
        }

        return dto;
    }

    @Override
    public List<FundWalletRequestDto> listRequestToRequestDto(List<FundWalletRequest> requests) {
        if (requests == null) {
            return null;
        } else {
            List<FundWalletRequestDto> list = new ArrayList(requests.size());

            for (FundWalletRequest fundWalletRequest : requests) {
                list.add(this.requestToRequestDto(fundWalletRequest));
            }

            return list;
        }
    }
}
