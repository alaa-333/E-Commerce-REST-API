package com.ecommerce.api.integration.controller;

import com.ecommerce.api.controller.ProductController;
import com.ecommerce.api.dto.request.product.CreateProductRequest;
import com.ecommerce.api.dto.request.product.ProductSearchCriteria;
import com.ecommerce.api.dto.request.product.UpdateProductRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.CategoryResponse;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.dto.response.ProductResponse;
import com.ecommerce.api.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponse sampleProductResponse;

    @BeforeEach
    void setUp()
    {

        sampleProductResponse = ProductResponse.builder()
                .id(1L)
                .name("Sample Product")
                .description("Sample Description")
                .price(new BigDecimal("29.99"))
                .stockQuantity(100)
                .category(CategoryResponse.builder().id(1L).name("Sample Category").build())
                .imageUrl("http://example.com/image.jpg")
                .active(true)
                .build();
    }

    @Nested
    @DisplayName("GET /products — list all products")
    class GetAllProducts
    {

        @Test
        @DisplayName("returns 200 OK with a page of products")
        void getAllProducts_returnsOk() throws Exception
        {

            var page = new PageImpl<>(List.of(sampleProductResponse));
            given(productService.getAllProducts(any(PageRequest.class)))
                    .willReturn(PagedResponse.from(page));

            mockMvc.perform(get("/products")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(productService, times(1)).getAllProducts(any(PageRequest.class));
        }

        @Test
        @DisplayName("uses default page=0 and size=10 when no params are given")
        void getAllProducts_usesDefaults() throws Exception
        {
            given(productService.getAllProducts(any(PageRequest.class)))
                    .willReturn(PagedResponse.from(new PageImpl<>(List.of())));

            mockMvc.perform(get("/products"))
                    .andExpect(status().isOk());

            verify(productService).getAllProducts(PageRequest.of(0, 10));
        }
    }

    @Nested
    @DisplayName("GET /products/{id} — fetch a single product")
    class GetProductById
    {

        @Test
        @DisplayName("returns 200 OK with the product when it exists")
        void getProductById_found_returnsOk() throws Exception
        {
            given(productService.getProduct(1L)).willReturn(sampleProductResponse);

            mockMvc.perform(get("/products/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Sample Product"));
        }

        @Test
        @DisplayName("returns 400 Bad Request when id is not positive")
        void getProductById_negativeId_returnsBadRequest() throws Exception
        {

            mockMvc.perform(get("/products/{id}", -1L))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).getProduct(anyLong());
        }


        @Nested
        @DisplayName("POST /products — create a product")
        class CreateProduct
        {

            @Test
            @DisplayName("returns 201 Created with the created product")
            void createProduct_validRequest_returnsCreated() throws Exception
            {

                var request = CreateProductRequest.builder()
                        .name("New Product")
                        .price(java.math.BigDecimal.valueOf(19.99))
                        .build();

                given(productService.createProduct(any(CreateProductRequest.class)))
                        .willReturn(sampleProductResponse);

                mockMvc.perform(post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.data.id").value(1));
            }

            @Test
            @DisplayName("returns 400 Bad Request when the request body fails @Valid checks")
            void createProduct_invalidRequest_returnsBadRequest() throws Exception
            {

                mockMvc.perform(post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                        .andExpect(status().isBadRequest());

                verify(productService, never()).createProduct(any());
            }
        }

        @Nested
        @DisplayName("PUT /products/{id} — update a product")
        class UpdateProduct
        {

            @Test
            @DisplayName("returns 204 No Content on successful update")
            void updateProduct_validRequest_returnsNoContent() throws Exception
            {
                var request = UpdateProductRequest.builder()
                        .name("Updated Name")
                        .build();

                mockMvc.perform(put("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isNoContent());

                verify(productService).updateProduct(eqLong(1L), any(UpdateProductRequest.class));
            }

            @Test
            @DisplayName("returns 400 Bad Request when id is not positive")
            void updateProduct_negativeId_returnsBadRequest() throws Exception
            {
                var request = UpdateProductRequest.builder().name("X").build();

                mockMvc.perform(put("/products/{id}", -5L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(productService, never()).updateProduct(anyLong(), any());
            }
        }

        @Nested
        @DisplayName("DELETE /products/{id} — delete a product")
        class DeleteProduct
        {

            @Test
            @DisplayName("returns 200 OK on successful delete")
            void deleteProduct_validId_returnsOk() throws Exception
            {
                mockMvc.perform(delete("/products/{id}", 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").value("product deleted successfully"));

                verify(productService).deleteProduct(1L);
            }

            @Test
            @DisplayName("returns 400 Bad Request when id is not positive")
            void deleteProduct_negativeId_returnsBadRequest() throws Exception
            {
                mockMvc.perform(delete("/products/{id}", 0L))
                        .andExpect(status().isBadRequest());

                verify(productService, never()).deleteProduct(anyLong());
            }
        }

        @Nested
        @DisplayName("GET /products/search — search products")
        class SearchProducts
        {


            @Test
            @DisplayName("returns 200 OK with matching products")
            void searchProducts_returnsOk() throws Exception
            {
                var criteria = ProductSearchCriteria.builder()
                        .keyword("phone")
                        .build();

                given(productService.searchProducts(any(ProductSearchCriteria.class), any(PageRequest.class)))
                        .willReturn(pagedResponseFrom(new PageImpl<>(List.of(sampleProductResponse))));

                mockMvc.perform(get("/products/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(criteria)))
                        .andExpect(status().isOk());
            }
        }

        private PagedResponse<ProductResponse> pagedResponseFrom(PageImpl<ProductResponse> page)
        {
            return PagedResponse.<ProductResponse>builder()
                    .data(page.getContent())
                    .pageNumber(page.getNumber())
                    .pageSize(page.getSize())
                    .totalElement(page.getTotalElements())
                    .build();
        }

        private long eqLong(long value)
        {
            return value;
        }
    }
}