package com.algaworks.algamoneyapi.resource;

import com.algaworks.algamoneyapi.event.ResourceCreatedEvent;
import com.algaworks.algamoneyapi.model.Customer;
import com.algaworks.algamoneyapi.repository.CustomerRepository;
import com.algaworks.algamoneyapi.service.CustomerService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerResource {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    ApplicationEventPublisher publisher;

    @GetMapping
    public ResponseEntity<List<Customer>> listCustomers() {
        List<Customer> customerList = customerRepository.findAll();
        return ResponseEntity.ok(customerList);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> findCustomerById(@PathVariable String customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> ResponseEntity.ok(customer))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer, HttpServletResponse response) {
        Customer createdCustomer = customerService.createCustomer(customer);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, createdCustomer.getCustomerId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable String customerId) {
        customerRepository.deleteById(customerId);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String customerId,@Valid @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(customerId, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @PutMapping("/{customerId}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateIsActive(@PathVariable String customerId, @RequestBody Boolean isActive) {
        customerService.updateIsActive(customerId, isActive);
    }
}
