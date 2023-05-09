package com.example.attestation.repositories;

import com.example.attestation.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    //поиск по части наименования вне зависимости от регистра
    List<Product> findByTitleContainingIgnoreCase(String name);

    //поиск по наименованию и фильтрация по диапазону цены
    @Query(value = "select * from product where (lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3)", nativeQuery = true)
    List<Product> findByTitleAndPriceGreaterThanEqualAAndPriceLessThanEqual(String title, float ot, float Do);

    //поиск по наименованию и фильтрация по диапазону и возрастанию цены
    @Query(value = "select * from product where (lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Product> findByTitleOrderByPriceAsc (String title, float ot, float Do);

    //поиск по наименованию и фильтрация по диапазону и убыванию цены
    @Query(value = "select * from product where (lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Product> findByTitleOrderByPriceDesc (String title, float ot, float Do);

    //поиск по наименованию и фильтрация по диапазону и возрастанию цены, и фильтрация по категориям
    @Query(value = "select * from product where category_id = ?4 and (lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Product> findByTitleAAndCategoryOrderByPriceAsc (String title, float ot, float Do, int category);

    //поиск по наименованию и фильтрация по диапазону и убыванию цены, и фильтрация по категориям
    @Query(value = "select * from product where category_id = ?4 and (lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Product> findByTitleAAndCategoryOrderByPriceDesc (String title, float ot, float Do, int category);
}
