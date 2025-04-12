package sv.edu.udb.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.edu.udb.dto.OrderDTO;
import sv.edu.udb.dto.OrderDetailDTO;
import sv.edu.udb.exception.InsufficientStockException;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Customer;
import sv.edu.udb.model.Order;
import sv.edu.udb.model.OrderDetail;
import sv.edu.udb.model.Product;
import sv.edu.udb.repository.OrderDetailRepository;
import sv.edu.udb.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ProductService productService;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }
    
    public List<Order> getOrdersByCustomer(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return orderRepository.findByCustomer(customer);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        Customer customer = customerService.getCustomerById(orderDTO.getCustomerId());
        
        Order order = new Order();
        order.setCustomer(customer);
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Save order first to get ID
        order = orderRepository.save(order);
        
        List<OrderDetail> orderDetails = new ArrayList<>();
        double total = 0.0;
        
        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            Product product = productService.getProductById(detailDTO.getProductId());
            
            // Check if product has enough stock
            if (product.getStock() < detailDTO.getQuantity()) {
                throw new InsufficientStockException("Stock insuficiente para el producto: " + product.getName());
            }
            
            // Update product stock
            boolean stockUpdated = productService.updateStock(product.getId(), detailDTO.getQuantity());
            
            if (!stockUpdated) {
                throw new InsufficientStockException("Error al actualizar el stock del producto: " + product.getName());
            }
            
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(product.getPrice());
            detail.setSubtotal(product.getPrice() * detailDTO.getQuantity());
            
            orderDetails.add(detail);
            total += detail.getSubtotal();
        }
        
        // Save all order details
        orderDetailRepository.saveAll(orderDetails);
        
        // Update order with total and details
        order.setOrderDetails(orderDetails);
        order.setTotal(total);
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
}
