package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.model.enums.PaginationColumnsWhiteList;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequestDto {

    @Min(value = 1, message = "page number must be greater than 0")
    private int page = 1;

    @Max(value = 100 , message = "size can not exceed 100 ")
    private int size = 10;


    private String sortBy = "id";
    private String sortDirection = "ASC";
    private String searchQuery;

    public Pageable toPageable ()
    {
        //ASSIGN SORT DIR
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ?
                 Sort.Direction.DESC: Sort.Direction.ASC;


        return PageRequest.of(page-1 , size , Sort.by(direction, sortBy));

    }



    public static void validate(PaginationRequestDto requestDto)
    {
        String apiField = requestDto.getSortBy();
        if (!PaginationColumnsWhiteList.isValid(apiField))
        {
            throw new ValidationException(
                    ErrorCode.INVALID_SORT_PARAMETER,
                    String.format("Invalid sort field '%s'. Allowed fields are: %s",
                            apiField,
                            String.join(", ", PaginationColumnsWhiteList.getAllowedFields()))
            );
        }

    }
}
