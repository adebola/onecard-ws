package io.factorialsystems.msscprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Null;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCategoryDto {
    @Null(message = "Category Id will be set automatically")
    Integer id;

    @NotNull(message = "Please set Category Name")
    String categoryName;

    @Null(message = "You cannot set the CreatedBy Field")
    private String createdBy;

    @Null(message = "Creation date will be set automatically")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date createdDate;
}
