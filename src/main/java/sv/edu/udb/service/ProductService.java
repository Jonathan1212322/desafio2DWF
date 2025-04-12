package sv.edu.udb.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Product;
import sv.edu.udb.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailableTrue();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setStock(productDetails.getStock());
        product.setAvailable(productDetails.isAvailable());
        
        return productRepository.save(product);
    }
    
    @Transactional
    public boolean updateStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        
        if (product.getStock() < quantity) {
            return false;
        }
        
        product.setStock(product.getStock() - quantity);
        
        if (product.getStock() == 0) {
            product.setAvailable(false);
        }
        
        productRepository.save(product);
        return true;
    }
    
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
