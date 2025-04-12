package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.exception.UniqueConstraintViolationException;
import sv.edu.udb.model.Customer;
import sv.edu.udb.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public List<Customer> getActiveCustomers() {
        return customerRepository.findByActiveTrue();
    }
    
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }
    
    public Customer createCustomer(Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
        
        if (existingCustomer.isPresent()) {
            throw new UniqueConstraintViolationException("Ya existe un cliente con el email: " + customer.getEmail());
        }
        
        return customerRepository.save(customer);
    }
    
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = getCustomerById(id);
        
        // Check if email is being changed and if it's already in use
        if (!customer.getEmail().equals(customerDetails.getEmail())) {
            Optional<Customer> existingCustomer = customerRepository.findByEmail(customerDetails.getEmail());
            
            if (existingCustomer.isPresent()) {
                throw new UniqueConstraintViolationException("Ya existe un cliente con el email: " + customerDetails.getEmail());
            }
        }
        
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setAddress(customerDetails.getAddress());
        customer.setActive(customerDetails.isActive());
        
        return customerRepository.save(customer);
    }
    
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
}
