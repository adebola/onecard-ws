package io.factorialsystems.msscwallet.web.mapper;


import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.web.model.TransactionDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {DateMapper.class})
public interface TransactionMapstructMapper {
    TransactionDto transactionToTransactionDto(Transaction transaction);
    List<TransactionDto> listTransactionToTransactionDto(List<Transaction> transactions);
}
