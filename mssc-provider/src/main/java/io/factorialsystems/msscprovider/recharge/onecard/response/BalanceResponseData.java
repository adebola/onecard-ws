package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceResponseData {
    @JsonProperty("RESPONSE_TOTAL")
    private String responseTotal;
    @JsonProperty("LOCK_FUND")
    private String lockFund;
    @JsonProperty("BALANCE")
    private String balance;
    @JsonProperty("DUE_CREDITS")
    private String dueCredits;
    @JsonProperty("CURRENCY_CODE")
    private String currencyCode;
    @JsonProperty( "CURRENCY_SIGN")
    private String currencySign;
    @JsonProperty("STOCK_BAL")
    private String stockBalance;
    @JsonProperty( "TOTAL_SALES")
    private String totalSales;
    @JsonProperty("TOTAL_PROFIT")
    private String totalProfit;
}
