package io.factorialsystems.msscprovider.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class Constants {
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final String PAYSTACK_PAY_MODE = "paystack";
    public static final String WALLET_PAY_MODE = "wallet";
    public static final String BULK_RECHARGE = "bulk";
    public static final String SINGLE_RECHARGE = "single";
    public static final String[] ALL_PAYMENT_MODES = {PAYSTACK_PAY_MODE, WALLET_PAY_MODE };
    public static final String HEADER_EMAIL = "email";
    public static final String HEADER_PASSWORD = "password";
    public static final int FIVE_MINUTES = 300;
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MULTIPART_REQUESTPART_NAME = "file";

    public static final String DATA_LABEL = "DATA";
    public static final String GLO_DATA_LABEL = "GLO-DATA";
    public static final String MTN_DATA_LABEL = "MTN-DATA";
    public static final String AIRTEL_DATA_LABEL = "AIRTEL-DATA";
    public static final String NINEMOBILE_DATA_LABEL = "9MOBILE-DATA";
    public static final String SPECTRANET_LABEL = "SPECTRANET";
    public static final String SPECTRANET_DATA_LABEL = "SPECTRANET-DATA";
    public static final String SMILE_LABEL = "SMILE";
    public static final String SMILE_DATA_LABEL = "SMILE-DATA";
    public static final String DSTV_LABEL = "DSTV";
    public static final String GOTV_LABEL = "GOTV";
    public static final String STARTIMES_LABEL = "STARTIMES";
    public static final String AIRTIME_LABEL = "AIRTIME";
    public static final String ELECTRICITY_LABEL = "ELECTRICITY";
    public static final String EKEDP_LABEL = "EKEDP";

    public static final BigDecimal MINIMUM_RECHARGE_VALUE = new BigDecimal(1);

}
