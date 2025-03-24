package com.example.FlowerShop.service;

import com.example.FlowerShop.dto.request.ProductRequest;
import com.example.FlowerShop.model.Product;
import com.example.FlowerShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

//    public Product createProduct(Product product) {
//        return productRepository.save(product);
//    }
public Product createProduct(ProductRequest req) {
    Optional<Product> productOpt = productRepository.findByName(req.getName());
    if(productOpt.isPresent()){
        throw new RuntimeException("Product already exists");
    }
    Product product = new Product();
    product.setName(req.getName());
    product.setDescription(req.getDescription());
    product.setPrice(req.getPrice());
    product.setStock(req.getStock());
    Product saveProduct = productRepository.save(product);

    return saveProduct;
}

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = productOpt.get();
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setDescription(updatedProduct.getDescription());
        product.setStock(updatedProduct.getStock());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
