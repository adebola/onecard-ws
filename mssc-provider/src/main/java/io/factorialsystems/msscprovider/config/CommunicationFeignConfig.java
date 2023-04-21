package io.factorialsystems.msscprovider.config;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.ContentType;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringFormEncoder;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import io.factorialsystems.msscprovider.external.decoder.FeignCustomErrorDecoder;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(JsonFormWriter.class)
public class CommunicationFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            final String token = ProviderSecurity.getAccessToken();
            if (token != null) {
                requestTemplate.header("Authorization", "Bearer " + ProviderSecurity.getAccessToken());
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder () {
        return new FeignCustomErrorDecoder();
    }

    @Bean
    Encoder feignEncoder(JsonFormWriter jsonFormWriter){
        return new SpringFormEncoder() {{
            var processor = (MultipartFormContentProcessor) getContentProcessor(ContentType.MULTIPART);
            processor.addFirstWriter(jsonFormWriter);
            processor.addFirstWriter(new SpringSingleMultipartFileWriter());
            processor.addFirstWriter(new SpringManyMultipartFilesWriter());
        }};
    }
}
