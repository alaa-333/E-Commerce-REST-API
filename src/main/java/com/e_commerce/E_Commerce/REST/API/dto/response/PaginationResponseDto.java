package com.e_commerce.E_Commerce.REST.API.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponseDto<T> {

    private List<T> data;
    private PaginationMetadata metadata;



    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata
    {
        private int currentPage;
        private int pageSize;
        private Long totalElement;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        private boolean isFirst;
        private boolean isLast;


        @JsonProperty("rangeStart")
        public int getRangeStart()
        {
            return (currentPage-1) * pageSize+1;
        }

        @JsonProperty("rangeEnd")
        public long getRangeEnd()
        {
            long end = (long) currentPage * pageSize;
            return Math.min(end, totalElement);
        }


        public static <T> PaginationResponseDto<T> of (Page<T> page)
        {

            PaginationMetadata metadata1 = new PaginationMetadata(
                    page.getNumber()+1,
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.hasNext(),
                    page.hasPrevious(),
                    page.isFirst(),
                    page.isLast()
            );
            return new PaginationResponseDto<>(page.getContent() , metadata1);
        }
    }
}
