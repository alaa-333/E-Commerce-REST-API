package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.product.ProductSearchCriteria;
import com.ecommerce.api.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationBuilder {

    public static Specification<Product> buildSpecification(ProductSearchCriteria criteria) {

        return ((root, query, criteriaBuilder) ->  {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%"+criteria.keyword().toLowerCase()+"%"
                        )
                );
            }

            if (criteria.categoryId() != null && criteria.categoryId() > 0) {
                predicates.add(
                    criteriaBuilder.equal(root.get("category").get("id"),
                                criteria.categoryId()
                        )
                );
            }
            if (criteria.maxPrice() != null && criteria.maxPrice().compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.maxPrice())
                );
            }

            if (criteria.minPrice() != null && criteria.minPrice().compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.minPrice())
                );
            }



            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        } );

    }
}
