package com.example.customers.service;

import com.example.customers.exception.CustomerNotFoundException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private List<Customer> sampleCustomers;

    @BeforeEach
    void setUp() {
        sampleCustomers = List.of(
            new Customer("Alice", "Smith", "alice@example.com"),
            new Customer("Bob", "Jones", "bob@example.com")
        );
    }

    @Test
    @DisplayName("getAllCustomers returns all customers from repository")
    void getAllCustomers_returnsAllCustomers() {
        when(customerRepository.findAll()).thenReturn(sampleCustomers);

        List<Customer> result = customerService.getAllCustomers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
        assertThat(result.get(1).getFirstName()).isEqualTo("Bob");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCustomers returns empty list when no customers exist")
    void getAllCustomers_returnsEmptyList_whenNoCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<Customer> result = customerService.getAllCustomers();

        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCustomers calls repository exactly once")
    void getAllCustomers_callsRepositoryOnce() {
        when(customerRepository.findAll()).thenReturn(sampleCustomers);

        customerService.getAllCustomers();

        verify(customerRepository, times(1)).findAll();
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    @DisplayName("addCustomer saves and returns the new customer")
    void addCustomer_savesAndReturnsCustomer() {
        Customer input = new Customer("Dave", "Brown", "dave@example.com");
        Customer saved = new Customer(4L, "Dave", "Brown", "dave@example.com");
        when(customerRepository.save(input)).thenReturn(saved);

        Customer result = customerService.addCustomer(input);

        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getFirstName()).isEqualTo("Dave");
        assertThat(result.getEmail()).isEqualTo("dave@example.com");
        verify(customerRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("addCustomer calls repository save exactly once")
    void addCustomer_callsRepositorySaveOnce() {
        Customer input = new Customer("Eve", "Green", "eve@example.com");
        when(customerRepository.save(any(Customer.class))).thenReturn(input);

        customerService.addCustomer(input);

        verify(customerRepository, times(1)).save(input);
        verifyNoMoreInteractions(customerRepository);
    }

    // --- getCustomerById ---

    @Test
    @DisplayName("getCustomerById returns customer when found")
    void getCustomerById_returnsCustomer_whenFound() {
        Customer customer = new Customer(1L, "Alice", "Smith", "alice@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Alice");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getCustomerById throws CustomerNotFoundException when not found")
    void getCustomerById_throwsException_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- updateCustomer ---

    @Test
    @DisplayName("updateCustomer updates and returns the customer when found")
    void updateCustomer_updatesAndReturnsCustomer_whenFound() {
        Customer existing = new Customer(1L, "Alice", "Smith", "alice@example.com");
        Customer update = new Customer("Alicia", "Smith-Jones", "alicia@example.com");
        Customer saved = new Customer(1L, "Alicia", "Smith-Jones", "alicia@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        Customer result = customerService.updateCustomer(1L, update);

        assertThat(result.getFirstName()).isEqualTo("Alicia");
        assertThat(result.getEmail()).isEqualTo("alicia@example.com");
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(existing);
    }

    @Test
    @DisplayName("updateCustomer throws CustomerNotFoundException when not found")
    void updateCustomer_throwsException_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(99L, new Customer("A", "B", "a@b.com")))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");

        verify(customerRepository, never()).save(any());
    }

    // --- deleteCustomer ---

    @Test
    @DisplayName("deleteCustomer deletes the customer when found")
    void deleteCustomer_deletesCustomer_whenFound() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCustomer throws CustomerNotFoundException when not found")
    void deleteCustomer_throwsException_whenNotFound() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");

        verify(customerRepository, never()).deleteById(any());
    }
}
