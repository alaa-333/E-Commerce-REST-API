package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.ProductResponseDTO;
import com.e_commerce.E_Commerce.REST.API.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Test")
class ProductControllerTest {

        @MockitoBean
        private ProductService service;

        @Autowired
        private MockMvc mockMvc;

        private ProductResponseDTO responseDTO;
        private ProductUpdateRequestDTO updateRequestDTO;
        private ProductCreateRequestDTO createRequestDTO;
        private PaginationResponseDto<ProductResponseDTO> paginationResponseDto;

        @BeforeEach
        void setUp() {
                responseDTO = ProductResponseDTO.builder()
                                .id(1L)
                                .name("laptop")
                                .category("electronic")
                                .description("the best laptop")
                                .price(new BigDecimal("1099.00"))
                                .stockQuantity(10)
                                .build();

                updateRequestDTO = new ProductUpdateRequestDTO();
                updateRequestDTO.setName("updated laptop");
                updateRequestDTO.setDescription("updated desc for laptop");
                updateRequestDTO.setPrice(new BigDecimal("2099.00"));
                updateRequestDTO.setStockQuantity(15);

                createRequestDTO = new ProductCreateRequestDTO();
                createRequestDTO.setName("laptop");
                createRequestDTO.setDescription("the best laptop");
                createRequestDTO.setPrice(new BigDecimal("1099.00"));
                createRequestDTO.setStockQuantity(10);

                paginationResponseDto = new PaginationResponseDto<>();
                paginationResponseDto.setData(List.of(responseDTO));
                paginationResponseDto.setMetadata(PaginationResponseDto.PaginationMetadata.builder()
                                .currentPage(1)
                                .pageSize(10)
                                .totalElement(1L)
                                .totalPages(1)
                                .isLast(true)
                                .build());
        }

        @Nested
        @DisplayName("Create Product Tests")
        class CreateProduct {

                @Test
                @DisplayName("Should create product when valid data is provided")
                void create_whenValidData_shouldBeCreated() throws Exception {
                        when(service.createProduct(any(ProductCreateRequestDTO.class))).thenReturn(responseDTO);

                        mockMvc.perform(post("/api/v1/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(new ObjectMapper().writeValueAsString(createRequestDTO)))
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id", is(1)))
                                        .andExpect(jsonPath("$.name").value("laptop"))
                                        .andExpect(jsonPath("$.description").value("the best laptop"))
                                        .andExpect(jsonPath("$.price").value(1099.00))
                                        .andExpect(jsonPath("$.stockQuantity").value(10));

                        verify(service, times(1)).createProduct(any(ProductCreateRequestDTO.class));
                }

                @Test
                @DisplayName("Should return BadRequest when invalid data is provided")
                void create_whenInvalidData_shouldReturnBadRequest() throws Exception {
                        ProductCreateRequestDTO requestDTO = new ProductCreateRequestDTO(); // Empty/Invalid

                        mockMvc.perform(post("/api/v1/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).createProduct(any(ProductCreateRequestDTO.class));
                }
        }

        @Nested
        @DisplayName("Get Product By Id Tests")
        class GetById {

                @Test
                @DisplayName("Should return product when it exists")
                void getById_whenProductExist_shouldReturnProduct() throws Exception {
                        when(service.getById(anyLong())).thenReturn(responseDTO);

                        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(1L));

                        verify(service, times(1)).getById(anyLong());
                }

                @Test
                @DisplayName("Should return BadRequest when ID is invalid")
                void getById_whenInvalidId_shouldReturnsBadRequest() throws Exception {
                        mockMvc.perform(get("/api/v1/products/{id}", -1))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getById(anyLong());
                }

                @Test
                @DisplayName("Should return BadRequest when ID is zero")
                void getById_whenIdIsZero_shouldReturnsBadRequest() throws Exception {
                        mockMvc.perform(get("/api/v1/products/{id}", 0))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getById(anyLong());
                }
        }

        @Nested
        @DisplayName("Update Product Tests")
        class UpdateProduct {

                @Test
                @DisplayName("Should update product when ID exists and data is valid")
                void updateProduct() throws Exception {
                        var productId = 2L;
                        ProductResponseDTO updatedResponse = ProductResponseDTO.builder()
                                        .id(productId)
                                        .name("updated laptop")
                                        .description("updated description")
                                        .price(new BigDecimal("3099.00"))
                                        .stockQuantity(15)
                                        .build();

                        when(service.updateProduct(any(ProductUpdateRequestDTO.class), eq(productId)))
                                        .thenReturn(updatedResponse);

                        mockMvc.perform(put("/api/v1/products/{id}", productId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(new ObjectMapper().writeValueAsString(updateRequestDTO)))
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(2))
                                        .andExpect(jsonPath("$.name").value("updated laptop"))
                                        .andExpect(jsonPath("$.description").value("updated description"))
                                        .andExpect(jsonPath("$.price").value(3099.00))
                                        .andExpect(jsonPath("$.stockQuantity").value(15));

                        verify(service, times(1)).updateProduct(any(ProductUpdateRequestDTO.class), eq(productId));
                }

                @Test
                @DisplayName("Should return BadRequest when ID is invalid")
                void updateProduct_whenInvalidId_shouldReturnBadRequest() throws Exception {
                        var invalidId = -8L;
                        mockMvc.perform(put("/api/v1/products/{id}", invalidId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(new ObjectMapper().writeValueAsString(updateRequestDTO)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).updateProduct(any(ProductUpdateRequestDTO.class), eq(invalidId));
                }
        }

        @Nested
        @DisplayName("Get All Products Tests")
        class GetAll {

                @Test
                @DisplayName("Should return products when pagination is valid")
                void getAll_whenValidPagination_shouldReturnProducts() throws Exception {
                        when(service.getAll(any(PaginationRequestDto.class)))
                                        .thenReturn(paginationResponseDto);

                        mockMvc.perform(get("/api/v1/products")
                                        .param("page", "1")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.data", hasSize(1)))
                                        .andExpect(jsonPath("$.data[0].name").value("laptop"))
                                        .andExpect(jsonPath("$.metadata.currentPage").value(1))
                                        .andExpect(jsonPath("$.metadata.pageSize", is(10)))
                                        .andExpect(jsonPath("$.metadata.totalElement", is(1)))
                                        .andExpect(jsonPath("$.metadata.totalPages", is(1)))
                                        .andExpect(jsonPath("$.metadata.isLast", is(true)));

                        verify(service, times(1)).getAll(any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return products with default pagination when criteria not provided")
                void getAll_whenDefaultPagination_shouldReturnProducts() throws Exception {
                        when(service.getAll(any(PaginationRequestDto.class)))
                                        .thenReturn(paginationResponseDto);

                        mockMvc.perform(get("/api/v1/products")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        verify(service, times(1)).getAll(any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return BadRequest when page number is negative")
                void getAll_whenNegativePageNumber_shouldReturnBadRequest() throws Exception {
                        mockMvc.perform(get("/api/v1/products")
                                        .param("page", "-1")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getAll(any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return BadRequest when page size is negative")
                void getAll_whenNegativePageSize_shouldReturnBadRequest() throws Exception {
                        mockMvc.perform(get("/api/v1/products")
                                        .param("page", "1")
                                        .param("size", "-1")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getAll(any(PaginationRequestDto.class));
                }
        }

        @Nested
        @DisplayName("Delete Product Tests")
        class DeleteProduct {

                @Test
                @DisplayName("Should delete product when ID exists")
                void delete_whenIdExists_shouldBeDeleted() throws Exception {
                        var deletedId = 1L;
                        doNothing().when(service).deleteProduct(deletedId);

                        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", deletedId)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isNoContent())
                                        .andExpect(content().string(""));

                        verify(service, times(1)).deleteProduct(deletedId);
                }

                @Test
                @DisplayName("Should return BadRequest when ID does not exist")
                void delete_whenIdInvalid_shouldReturnBadRequest() throws Exception {
                        var invalidId = -1L;

                        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", invalidId)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).deleteProduct(invalidId);
                }
        }

        @Nested
        @DisplayName("Filter Products Tests")
        class FilterProducts {

                @Test
                @DisplayName("Should return products filtered by price range")
                void filteredProductsByPrice_whenValidPriceRange_shouldReturnProducts() throws Exception {
                        var minPrice = new BigDecimal("1000.00");
                        var maxPrice = new BigDecimal("1199.00");
                        var page = "1";
                        var size = "10";

                        when(service.getByRangePrice(minPrice, maxPrice, any(PaginationRequestDto.class)))
                                        .thenReturn(paginationResponseDto);

                        mockMvc.perform(get("/api/v1/products/price")
                                        .param("minPrice", String.valueOf(minPrice))
                                        .param("max", String.valueOf(maxPrice))
                                        .param("page", page)
                                        .param("size", size))
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        verify(service, times(1)).getByRangePrice(minPrice, maxPrice, any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return BadRequest when price range is negative")
                void filteredProductsByPrice_whenNegativePriceRange_shouldReturnBadRequest() throws Exception {
                        var minPrice = new BigDecimal("1000.00");
                        var maxPrice = new BigDecimal("-1199.00");

                        mockMvc.perform(get("/api/v1/products/price")
                                        .param("minPrice", String.valueOf(minPrice))
                                        .param("max", String.valueOf(maxPrice))
                                        .param("page", "1")
                                        .param("size", "10"))
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getByRangePrice(minPrice, maxPrice, any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return products filtered by name")
                void filteredProductsByName_whenNameExist_shouldReturnProducts() throws Exception {
                        var nameParam = "laptop";

                        when(service.getProductsByName(eq(nameParam), any(PaginationRequestDto.class)))
                                        .thenReturn(paginationResponseDto);

                        mockMvc.perform(get("/api/v1/products/name")
                                        .param("name", nameParam)
                                        .param("page", "1")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.data", hasSize(1)))
                                        .andExpect(jsonPath("$.data[0].name", is("laptop")));

                        verify(service).getProductsByName(eq(nameParam), any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return BadRequest when name is missing")
                void filteredProductsByName_whenMissingName_shouldReturnBadRequest() throws Exception {
                        mockMvc.perform(get("/api/v1/products/name")
                                        .param("page", "1")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getProductsByName(anyString(), any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return BadRequest when name is blank")
                void filteredProductsByName_whenBlankName_shouldReturnsBadRequest() throws Exception {
                        mockMvc.perform(get("/api/v1/products/name")
                                        .param("name", "   ")
                                        .param("page", "1")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getProductsByName(anyString(), any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return products filtered by category")
                void getByCategory_whenCategoryExist_shouldReturnProducts() throws Exception {
                        var category = "electronic";
                        when(service.getProductsByCategory(eq(category), any(PaginationRequestDto.class)))
                                        .thenReturn(paginationResponseDto);

                        mockMvc.perform(get("/api/v1/products/category")
                                        .param("category", category)
                                        .param("page", "1")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.data", hasSize(1)))
                                        .andExpect(jsonPath("$.metadata.currentPage", is(1)));

                        verify(service, times(1)).getProductsByCategory(anyString(), any(PaginationRequestDto.class));
                }

                @Test
                @DisplayName("Should return BadRequest when category is missing")
                void getByCategory_whenMissingCategory_shouldReturnBadRequest() throws Exception {
                        mockMvc.perform(get("/api/v1/products/category")
                                        .param("page", "1")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                        verify(service, never()).getProductsByCategory(anyString(), any(PaginationRequestDto.class));
                }
        }
}