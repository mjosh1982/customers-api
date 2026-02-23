package com.example.customers;

import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        saveIfNotExists("alice@example.com", new Customer("Alice", "Smith", "alice@example.com"));
        saveIfNotExists("bob@example.com", new Customer("Bob", "Jones", "bob@example.com"));
        saveIfNotExists("carol@example.com", new Customer("Carol", "White", "carol@example.com"));
    }

    private void saveIfNotExists(String email, Customer customer) {
        if (!customerRepository.existsByEmail(email)) {
            customerRepository.save(customer);
        }
    }
}
