package com.example.attestation.services;

import com.example.attestation.models.Category;
import com.example.attestation.models.Product;
import com.example.attestation.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //метод возвращает список всех товаров
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    //метод получает товар по id
    public Product getProductId(int id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    //метод сохраняет товар, привязав к нему категорию
    @Transactional
    public void saveProduct(Product product, Category category){
        product.setCategory(category);
        productRepository.save(product);
    }

    //метод обновляет товар по id
    @Transactional
    public void updateProduct(int id, Product product){
        product.setId(id);
        productRepository.save(product);
    }

    //удаление товара по id
    @Transactional
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }

}
