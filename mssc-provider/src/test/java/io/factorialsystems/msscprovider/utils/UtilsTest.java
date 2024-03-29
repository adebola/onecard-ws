package io.factorialsystems.msscprovider.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoAirtimeRequest;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@CommonsLog
//@Slf4j
public class UtilsTest {
    final String ALGORITHM = "PBEWithMD5AndDES";
    final int ITERATION_COUNT = 1000;

    @Test
    public void prettyPrint() throws JsonProcessingException {
        Logger logger = Logger.getLogger(UtilsTest.class.getName());
        RingoAirtimeRequest airtimeRequest = RingoAirtimeRequest.builder()
                .amount(String.valueOf("1000"))
                .request_id(UUID.randomUUID().toString())
                .msisdn("08012345678")
                .serviceCode("ADA")
                .product_id("MD1")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(airtimeRequest);
        final String s = String.format("\n%s", prettyJson);
        logger.info(s);
    }

    @Test
    public void testDate() {
        Date d = new Date();
        Date e = new Date();


        log.info(d);
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        log.info(d);
        log.info(e);

        e.setHours(23);
        e.setMinutes(59);
        e.setSeconds(59);
        log.info(e);
//
//        String file1 = String.format("%d-%d-%d-to-date.xls", d.getDate(), d.getMonth(), d.getYear() + 1900);
//
//        String file2 = String.format("%d-%d-%d-to-%d-%d-%d.xls", d.getDate(), d.getMonth(), d.getYear() + 1900,
//                e.getDate(), e.getMonth(), e.getYear() + 1900);
//
//        log.info(file1);
//        log.info(file2);
    }


    @Test
    public void lastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        log.info(cal);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        int lastDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        log.info(lastDayOfMonth);
    }

    @Test
    public void calendarTest() {
        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(new Date());

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);

        log.info(String.format("Day of Week %d", dayOfWeek));
        log.info(String.format("Day of Month %d", dayOfMonth));
        log.info(String.format("Week of Year %d", weekOfYear));
        log.info(String.format("Month of Year %d", monthOfYear));
    }

    @Test
    void flipBits() {
        long x = 3L;

        log.info(x>>1);
        log.info(x<<1);
        log.info(x & 1);
        log.info(~x);

        long b = 0L;
        while (x!=0){
            b|=( x &1);
            x>>>=1;
            b<<=1;
        }

        log.info(b);

    }

//    @Test
//    public void mapTest() {
//        Student student = new Student();
//
//        var y = student.getAccount();
//        var z = y.get().getLoan();
//
//        var x = Optional.of(student)
//                .flatMap(Student::getAccount)
//                .flatMap(Account::getLoan)
//                .map(Loan::getAmount)
//                .orElse(0d);
//
//        List<Student> students = Arrays.asList(new Student(), new Student());
//
//        var a = students.stream()
//                .map(Student::getAccount)
//                .map(account -> account.get().getLoan())
//                .map(account -> account.flatMap(Account::getLoan))
//
//    }


    @Test
    void dateTest() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final Date expiry = simpleDateFormat.parse("2023-07-20 23:45:24");
        final Date end = simpleDateFormat.parse("2023-07-20 19:00:24");

        final int i = expiry.compareTo(end);
        log.info("Results");
        log.info(i);

    }


    @Test
    void bigDecimalTest() {
        BigDecimal b = new BigDecimal(1500);
        BigDecimal c = new BigDecimal(10);

        log.info(c.compareTo(b));
    }

    @Test
    void decrypt() throws Exception {

        String encryptedData = "BVrqLDQ8bnT8Yv50Xt2hsv/yMKiAH3whvKgQmet7eEFQencL8SXOQJ9WuweVUKAW9bD3YPRy5i6vaMmwAQXik9emEYP35FxCJpMMFu7DqH8=%";  // Encrypted data as byte array
        String key = "6458f080-555c-43ca-beb6-47c00a40";  // Password used for encryption
        String salt = "481efbbb87cf2b3e";  // Salt used for encryption

//        try {
//            String decryptedText = decrypt(encryptedData.getBytes(), password.toCharArray(), salt.getBytes());
//            log.info(decryptedText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        SecretKey symmetricKey = generateKey();
        IvParameterSpec iv = generateIv();


        // Encrypt the message using the symmetric key

        SecretKey secretKey = new SecretKey() {
            @Override
            public String getAlgorithm() {
                return null;
            }

            @Override
            public String getFormat() {
                return null;
            }

            @Override
            public byte[] getEncoded() {
                return new byte[0];
            }
        };
        // Decrypt the encrypted message
//        String decryptedText = decrypt(encryptedData.getBytes(),  iv.getIV());
    }

//    private String decrypt(byte[] encryptedData, char[] password, byte[] salt) throws Exception {
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
//        KeySpec keySpec = new PBEKeySpec(password, salt, ITERATION_COUNT);
//        SecretKey secretKey = keyFactory.generateSecret(keySpec);
//
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
//
//        byte[] decryptedData = cipher.doFinal(encryptedData);
//        return new String(decryptedData, StandardCharsets.UTF_8);
//    }


    @Test
    public void calPoints() {
//        final String[] ops = {"5","2","C","D","+"};
        final String[] ops = {"5","-2","4","C","D","9","+","+"};
        int[] points = new int[ops.length];

        int j = 0;
        for (int i=0; i<ops.length; i++) {

            if (i == 0) {
                points[i] = Integer.parseInt(ops[i]);
                j++;
                continue;
            }

            switch (ops[i]) {
                case "C":
                    points[j-1] = 0;
                    j=j-2;
                    break;
                case "D":
                    points[j] = points[j - 1] * 2;
                    break;
                case "+":
                    points[j] = points[j - 1] + points[j - 2];
                    break;
                default:
                    points[j] = Integer.parseInt(ops[i]);
                    break;
            }

            j++;
        }

        int sum = Arrays.stream(points).sum();
        log.info("Sum is");
        log.info(sum);
    }




    private  SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
        keygenerator.init(128);
        return keygenerator.generateKey();
    }

    private IvParameterSpec generateIv() {
        byte[] initializationVector = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initializationVector);
        return new IvParameterSpec(initializationVector);
    }

    private String decrypt(byte[] cipherText, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText);
    }

    private byte[] encrypt(String input, SecretKey key, IvParameterSpec iv)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
    }
}
