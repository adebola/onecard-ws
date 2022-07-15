package io.factorialsystems.msscwallet.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.dao.TransactionMapper;
import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.domain.query.SearchByDateRange;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.mapper.TransactionMapstructMapper;
import io.factorialsystems.msscwallet.service.file.ExcelWriter;
import io.factorialsystems.msscwallet.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final ExcelWriter excelWriter;
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

    public ByteArrayInputStream generateExcelTransactionFile(DateRangeDto dto)  {

        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");

        try {
            Date from = dto.getFrom() == null ? new Date(ft.parse("1970-01-01").getTime()) : dto.getFrom();
            Date to = dto.getTo() == null ? new Date(new Date().getTime() + (1000 * 60 * 60 * 24)): dto.getTo();

            SearchByDateRange dateRange = new SearchByDateRange();
            dateRange.setFromTs(from);
            dateRange.setToTs(to);
            dateRange.setUserId(K.getUserId());

            List<Transaction> transactions = transactionMapper.findUserTransactionByDateRange(dateRange);

            if (transactions == null || transactions.isEmpty()) {
                return new ByteArrayInputStream(new byte[0]);
            }

            return excelWriter.WriteTransactions(transactions, dto);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
