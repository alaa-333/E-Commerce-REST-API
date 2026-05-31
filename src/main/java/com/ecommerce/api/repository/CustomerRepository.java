package com.ecommerce.api.repository;

import com.ecommerce.api.entity.Address;
import com.ecommerce.api.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Page<Customer> findAll(Pageable pageable);

    @Modifying
    @Query("update Customer c set c.phone = :phone, c.address = :address where c.id = :id")
    int updatePhoneAndAddressById(@Param("id") Long id, @Param("phone") String phone, @Param("address") Address address);
}
