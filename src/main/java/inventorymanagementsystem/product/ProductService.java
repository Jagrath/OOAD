package inventorymanagementsystem.product;

import inventorymanagementsystem.category.CategoryRepository;
import inventorymanagementsystem.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void createProduct(Product product) {
        // Check for duplicate product name globally, as ownership has been removed
        if (productRepository.existsByName(product.getName())) {
            logger.info("Product name {} already in use, throwing exception", product.getName());
            throw new ProductNameTakenException();
        }
        if (product.getCategory() != null && !categoryRepository.existsById(product.getCategory().getId())) {
            logger.info("Product category with id {} not found, throwing exception", product.getCategory().getId());
            throw new InvalidCategoryException();
        }
        productRepository.save(product);
        logger.info("Product {} created", product.getName());
    }

    @Transactional(readOnly = true)
    public Page<Product> listProducts(int page) {
        logger.info("Listing products paginated");
        return productRepository.findAll(PageRequest.of(page - 1, 8, Sort.by("name")));
    }

    @Transactional(readOnly = true)
    public List<Product> listProducts() {
        logger.info("Listing all products sorted by name");
        return productRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public List<Product> findProducts(String name) {
        logger.info("Finding products containing name {}", name);
        return productRepository.findAllByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Product findProduct(long id) {
        logger.info("Finding product with id {}", id);
        return productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
    }

    @Transactional
    public void updateProduct(long id, Product updatedProduct) {
        var product = productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
        if (!product.getName().equals(updatedProduct.getName()) 
            && productRepository.existsByName(updatedProduct.getName())) {
            logger.info("New product name {} already in use, throwing exception", updatedProduct.getName());
            throw new ProductNameTakenException();
        }
        if (updatedProduct.getCategory() != null && !categoryRepository.existsById(updatedProduct.getCategory().getId())) {
            logger.info("New product category with id {} not found, throwing exception", updatedProduct.getCategory().getId());
            throw new InvalidCategoryException();
        }
        product.setName(updatedProduct.getName());
        product.setCategory(updatedProduct.getCategory());
        product.setQuantity(updatedProduct.getQuantity());
        product.setPrice(updatedProduct.getPrice());
        productRepository.save(product);
        logger.info("Product with id {} updated", product.getId());
    }

    @Transactional
    public void deleteProduct(long id) {
        if (!productRepository.existsById(id)) {
            logger.info("Product with id {} not found, throwing exception", id);
            throw new ProductNotFoundException();
        }
        if (orderRepository.existsByItemsProductId(id)) {
            logger.info("Product with id {} is being used in orders, throwing exception", id);
            throw new ProductDeletionNotAllowedException();
        }
        productRepository.deleteById(id);
        logger.info("Product with id {} deleted", id);
    }
}
