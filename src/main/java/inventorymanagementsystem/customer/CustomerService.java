package inventorymanagementsystem.customer;

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
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void createCustomer(Customer customer) {
        if (customerRepository.existsByName(customer.getName())) {
            logger.info("Customer name {} already in use, throwing exception", customer.getName());
            throw new CustomerNameTakenException();
        }
        customerRepository.save(customer);
        logger.info("Customer {} created", customer.getName());
    }

    @Transactional
    public void createAllCustomers(List<Customer> customers) {
        logger.info("Creating multiple customers, duplicates will be ignored");
        var newCustomers = customers.stream()
                .filter(customer -> !customerRepository.existsByName(customer.getName()))
                .toList();
        customerRepository.saveAll(newCustomers);
        newCustomers.forEach(customer -> logger.info("Customer {} created", customer.getName()));
    }

    @Transactional(readOnly = true)
    public Page<Customer> listCustomers(int page) {
        logger.info("Listing customers paginated");
        return customerRepository.findAll(PageRequest.of(page - 1, 8, Sort.by("name")));
    }

    @Transactional(readOnly = true)
    public List<Customer> listCustomers() {
        logger.info("Listing customers");
        return customerRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public List<Customer> findCustomers(String name) {
        logger.info("Finding customers containing name {}", name);
        return customerRepository.findAllByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Customer findCustomer(long id) {
        logger.info("Finding customer with id {}", id);
        return customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
    }

    @Transactional
    public void updateCustomer(long id, Customer updatedCustomer) {
        var customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
        if (!customer.getName().equals(updatedCustomer.getName())
            && customerRepository.existsByName(updatedCustomer.getName())) {
            logger.info("New customer name {} already in use, throwing exception", updatedCustomer.getName());
            throw new CustomerNameTakenException();
        }
        customer.setName(updatedCustomer.getName());
        customer.setAddress(updatedCustomer.getAddress());
        customer.setPhone(updatedCustomer.getPhone());
        customerRepository.save(customer);
        logger.info("Customer with id {} updated, new name is {}", customer.getId(), updatedCustomer.getName());
    }

    @Transactional
    public void deleteCustomer(long id) {
        if (!customerRepository.existsById(id)) {
            logger.info("Customer with id {} not found, throwing exception", id);
            throw new CustomerNotFoundException();
        }
        if (orderRepository.existsByCustomerId(id)) {
            logger.info("Customer with id {} still has orders, throwing exception", id);
            throw new CustomerDeletionNotAllowedException();
        }
        customerRepository.deleteById(id);
        logger.info("Customer with id {} deleted", id);
    }
}
