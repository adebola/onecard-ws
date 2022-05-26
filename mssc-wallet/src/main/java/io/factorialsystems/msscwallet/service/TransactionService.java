package io.factorialsystems.msscwallet.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.dao.TransactionMapper;
import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.dto.PagedDto;
import io.factorialsystems.msscwallet.dto.TransactionDto;
import io.factorialsystems.msscwallet.mapper.TransactionMapstructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionMapper transactionMapper;
    private final TransactionMapstructMapper transactionMapstructMapper;

    public PagedDto<TransactionDto> findUserTransactions(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Transaction> transactions = transactionMapper.findUserTransactions(id);

        return createDto(transactions);
    }

    public PagedDto<TransactionDto> findOrganizationTransactionsByAccountId(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Transaction> transactions = transactionMapper.findOrganizationTransactionsByAccountId(id);

        return  createDto(transactions);
    }

    public TransactionDto findTransaction(String id) {
        return transactionMapstructMapper.transactionToTransactionDto(transactionMapper.findTransaction(id));
    }

    private PagedDto<TransactionDto> createDto(Page<Transaction> transactions) {
        PagedDto<TransactionDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) transactions.getTotal());
        pagedDto.setPageNumber(transactions.getPageNum());
        pagedDto.setPageSize(transactions.getPageSize());
        pagedDto.setPages(transactions.getPages());
        pagedDto.setList(transactionMapstructMapper.listTransactionToTransactionDto(transactions.getResult()));
        return pagedDto;
    }
}
