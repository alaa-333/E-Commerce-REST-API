package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.ProductResponseDTO;
import com.e_commerce.E_Commerce.REST.API.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockitoBean // create a mock instance and put it into spring context
    private  ProductService service;


    @Autowired
    private  MockMvc mockMvc; // represent a mock http request



    // test data i need for testingg
    private ProductResponseDTO responseDTO;
    private ProductUpdateRequestDTO updateRequestDTO;
    private ProductCreateRequestDTO createRequestDTO;
    private PaginationResponseDto<ProductResponseDTO>  paginationResponseDto;


    @BeforeEach
    void setUp()
    {
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
        paginationResponseDto.setMetadata( PaginationResponseDto.PaginationMetadata.builder()
                .currentPage(1)
                .pageSize(10)
                .totalElement(1L)
                .totalPages(1)
                .isLast(true)
                .build()
        );


    }


    // ======================= create ===========================
    @Test
    void create_whenValidData_shouldBeCreated() throws Exception{

        when(service.createProduct(any(ProductCreateRequestDTO.class))).thenReturn(responseDTO);

        // simulate http req
        mockMvc.perform(post("/api/v1/products"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1L)))
                .andExpect(jsonPath("$.name").value("laptop"))
                .andExpect(jsonPath("$.description").value("the best laptop"))
                .andExpect(jsonPath("$.price" ).value(1099.00))
                .andExpect(jsonPath("$.stockQuantity").value(10));


        verify(service,times(1)).createProduct(any(ProductCreateRequestDTO.class));



    }

    @Test
    void create_whenInvalidData_shouldReturnBadRequest() throws Exception {
        ProductCreateRequestDTO requestDTO = new ProductCreateRequestDTO();

        mockMvc.perform(post("/api/v1/products"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, never()).createProduct(any(ProductCreateRequestDTO.class));
    }



    // ======================= getById ===========================
    @Test
    void getById_whenProductExist_shouldReturnProduct() throws Exception   {

        when(service.getById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/products/{id}",1L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));


        verify(service,times(1)).getById(anyLong());
    }

    @Test
    void getById_whenInvalidId_shouldReturnsBadRequest() throws Exception {

        when(service.getById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/products/{id}", -1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service,never()).getById(anyLong());
    }

    @Test
    void getById_whenIdIsZero_shouldReturnsBadRequest() throws Exception {

        when(service.getById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/products/{id}", 0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service,never()).getById(anyLong());
    }





    // ======================= updateProduct ===========================

    @Test
    void updateProduct() throws Exception {

        var productId = 2L;
        ProductResponseDTO updatedResponse = ProductResponseDTO.builder()
                .id(productId)
                .name("updatd laptop")
                .description("updated description")
                .price(new BigDecimal("3099.00"))
                .stockQuantity(15)
                .build();

        when(service.updateProduct(any(ProductUpdateRequestDTO.class) , eq(productId)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("api/v1/products/{id}", is(productId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated laptop"))
                .andExpect(jsonPath("$.description").value("updated description"))
                .andExpect(jsonPath("$.price").value(3099.00))
                .andExpect(jsonPath("$.stockQuantity").value(15));

        verify(service, times(1)).updateProduct(any(ProductUpdateRequestDTO.class),eq(productId));
    }

    @Test
    void updateProduct_whenInvalidId_shouldReturnBadRequest() throws Exception {
        var invalidId  = -8L;
        mockMvc.perform(put("api/v1/products/{id}", is(invalidId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, never()).updateProduct(any(ProductUpdateRequestDTO.class), eq(invalidId));
    }



    // ======================= getAll ===========================

    @Test
    void getAll_whenValidPagination_shouldReturnProducts() throws Exception {

        when(service.getAll(any(PaginationRequestDto.class)))
                .thenReturn(paginationResponseDto);

        mockMvc.perform(get("/api/v1/products")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data",hasSize(1)))
                .andExpect(jsonPath(("$.data[0].name")).value("laptop"))
                .andExpect(jsonPath("$.metadata.currentPage").value(1))
                .andExpect(jsonPath("$.metadata.pageSize", is(10)))
                .andExpect(jsonPath("$.metadata.totalElement", is(1)))
                .andExpect(jsonPath("$.metadata.totalPages", is(1)))
                .andExpect(jsonPath("$.metadata.isLast", is(true)));

        verify(service, times(1)).getAll(any(PaginationRequestDto.class));

    }

    @Test
    void getAll_whenDefaultPagination_shouldReturnProducts() throws Exception {
        when(service.getAll(any(PaginationRequestDto.class)))
                .thenReturn(paginationResponseDto);

        mockMvc.perform(get("api/v1/products"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).getAll(any(PaginationRequestDto.class));
    }

    @Test
    void getAll_whenNegativePageNumber_shouldReturnBadRequest() throws Exception
    {

        mockMvc.perform(get("api/v1/products").param("page", "-1").param("size", "10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service,never()).getAll(any(PaginationRequestDto.class));
    }
    @Test
    void getAll_whenNegativePageSize_shouldReturnBadRequest() throws Exception
    {

        mockMvc.perform(get("api/v1/products").param("page","1").param("size", "-1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service,never()).getAll(any(PaginationRequestDto.class));
    }


    // ======================= delete ===========================

    @Test
    void delete_whenIdExists_shouldBeDeleted() throws Exception {
        var deletedId = 1L;
        doNothing().when(service).deleteProduct(deletedId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}" ,is(deletedId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));  // No response body

        verify(service, times(1)).deleteProduct(deletedId);


    }

    @Test
    void delete_whenIdDoesNotExists_shouldBeDeleted() throws Exception {
        var nonExistId = 22L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}" ,is(nonExistId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, never()).deleteProduct(nonExistId);


    }



    // ======================= filteredProductsByPrice ===========================

    @Test
    void filteredProductsByPrice_whenValidPriceRange_shouldReturnProducts() throws Exception {

        var minPrice = new BigDecimal("1000.00");
        var maxPrice = new BigDecimal("1199.00");

        var page = "1";
        var size = "10";

        when(service.getByRangePrice(minPrice, maxPrice, any(PaginationRequestDto.class)))
                .thenReturn(paginationResponseDto);

        mockMvc.perform(get("api/v1/products/price")
                .param("minPrice", String.valueOf(minPrice))
                .param("max", String.valueOf(maxPrice))
                .param("page", page)
                .param("size", size)
        ).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).getByRangePrice(minPrice, maxPrice, any(PaginationRequestDto.class));
    }

    @Test
    void filteredProductsByPrice_whenNegativePriceRange_shouldReturnBadRequest() throws Exception {

        var minPrice = new BigDecimal("1000.00");
        var maxPrice = new BigDecimal("-1199.00");

        var page = "1";
        var size = "10";



        mockMvc.perform(get("api/v1/products/price")
                        .param("minPrice", String.valueOf(minPrice))
                        .param("max", String.valueOf(maxPrice))
                        .param("page", page)
                        .param("size", size)
                ).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service,never()).getByRangePrice(minPrice, maxPrice, any(PaginationRequestDto.class));



    }

    @Test
    void filteredProductsByName_whenNameExist_shouldReturnProducts() throws Exception {

        var nameParam = "laptop";
        var page = "1";
        var size = "10";

        when(service.getProductsByName(nameParam, any(PaginationRequestDto.class)))
                .thenReturn(paginationResponseDto);

        mockMvc.perform(get("api/v1/products/name")
                .param("name",nameParam)
                .param("page", page)
                .param("size", size)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data",hasSize(1)))
                .andExpect(jsonPath("$.data[0].name",is("laptop")));


        verify(service).getProductsByName(eq(nameParam), any(PaginationRequestDto.class));
    }

    @Test
    void filteredProductsByName_whenMissingName_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("api/v1/products/name")
                .param("page","1")
                .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());

        verify(service, never()).getProductsByName((anyString(), any(PaginationRequestDto.class));

    }

    @Test
    void filteredProductsByName_whenBlankName_shouldReturnsBadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/products/name")
                        .param("name", "   ")
                        .param("pageNumber", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, never())
                .getProductsByName(anyString(), any(PaginationRequestDto.class));
    }




    // ======================= getByCategory ===========================

    @Test
    void getByCategory_whenCategoryExist_shouldReturnProducts() throws Exception {

        var category = "electronic";
        when(service.getProductsByCategory(eq(category), any(PaginationRequestDto.class)))
                .thenReturn(paginationResponseDto);

        mockMvc.perform(get("api/v1/products/category", is(category))
                .param("category",category)
                .param("page","1")
                .param("size","10")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.metadata.currentPage",is(1)));

        verify(service,times(1))
                .getProductsByCategory(anyString(), any(PaginationRequestDto.class));
    }

    @Test
    void getByCategory_whenMissingCategory_shouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/products/category")
                        .param("pageNumber", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(service, never())
                .getProductsByCategory(anyString(), any(PaginationRequestDto.class));
    }
}