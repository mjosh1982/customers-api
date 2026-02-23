package com.example.customers.controller;

import com.example.customers.exception.CustomerNotFoundException;
import com.example.customers.model.Customer;
import com.example.customers.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("GET /api/customers returns 200 with list of customers")
    void getAllCustomers_returns200WithCustomers() throws Exception {
        List<Customer> customers = List.of(
            new Customer("Alice", "Smith", "alice@example.com"),
            new Customer("Bob", "Jones", "bob@example.com")
        );
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("Alice")))
            .andExpect(jsonPath("$[0].lastName", is("Smith")))
            .andExpect(jsonPath("$[0].email", is("alice@example.com")))
            .andExpect(jsonPath("$[1].firstName", is("Bob")));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("GET /api/customers returns 200 with empty list when no customers")
    void getAllCustomers_returns200WithEmptyList() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/api/customers")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/customers returns JSON content type")
    void getAllCustomers_returnsJsonContentType() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/api/customers"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/customers returns 201 with saved customer")
    void addCustomer_returns201WithSavedCustomer() throws Exception {
        Customer saved = new Customer(4L, "Dave", "Brown", "dave@example.com");
        when(customerService.addCustomer(any(Customer.class))).thenReturn(saved);

        String requestBody = """
                {
                    "firstName": "Dave",
                    "lastName": "Brown",
                    "email": "dave@example.com"
                }
                """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(4)))
            .andExpect(jsonPath("$.firstName", is("Dave")))
            .andExpect(jsonPath("$.lastName", is("Brown")))
            .andExpect(jsonPath("$.email", is("dave@example.com")));

        verify(customerService, times(1)).addCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("POST /api/customers calls service once")
    void addCustomer_callsServiceOnce() throws Exception {
        Customer saved = new Customer(5L, "Eve", "Green", "eve@example.com");
        when(customerService.addCustomer(any(Customer.class))).thenReturn(saved);

        String requestBody = """
                {
                    "firstName": "Eve",
                    "lastName": "Green",
                    "email": "eve@example.com"
                }
                """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());

        verify(customerService, times(1)).addCustomer(any(Customer.class));
        verifyNoMoreInteractions(customerService);
    }

    // --- GET /api/customers/{id} ---

    @Test
    @DisplayName("GET /api/customers/{id} returns 200 with customer when found")
    void getCustomerById_returns200_whenFound() throws Exception {
        Customer customer = new Customer(1L, "Alice", "Smith", "alice@example.com");
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        mockMvc.perform(get("/api/customers/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("Alice")))
            .andExpect(jsonPath("$.email", is("alice@example.com")));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("GET /api/customers/{id} returns 404 when not found")
    void getCustomerById_returns404_whenNotFound() throws Exception {
        when(customerService.getCustomerById(99L)).thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(get("/api/customers/99")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", containsString("99")));
    }

    // --- PUT /api/customers/{id} ---

    @Test
    @DisplayName("PUT /api/customers/{id} returns 200 with updated customer when found")
    void updateCustomer_returns200_whenFound() throws Exception {
        Customer updated = new Customer(1L, "Alicia", "Smith", "alicia@example.com");
        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(updated);

        String requestBody = """
                {
                    "firstName": "Alicia",
                    "lastName": "Smith",
                    "email": "alicia@example.com"
                }
                """;

        mockMvc.perform(put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Alicia")))
            .andExpect(jsonPath("$.email", is("alicia@example.com")));

        verify(customerService).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} returns 404 when not found")
    void updateCustomer_returns404_whenNotFound() throws Exception {
        when(customerService.updateCustomer(eq(99L), any(Customer.class)))
                .thenThrow(new CustomerNotFoundException(99L));

        String requestBody = """
                {
                    "firstName": "X",
                    "lastName": "Y",
                    "email": "x@y.com"
                }
                """;

        mockMvc.perform(put("/api/customers/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", containsString("99")));
    }

    // --- DELETE /api/customers/{id} ---

    @Test
    @DisplayName("DELETE /api/customers/{id} returns 204 when found")
    void deleteCustomer_returns204_whenFound() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
            .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} returns 404 when not found")
    void deleteCustomer_returns404_whenNotFound() throws Exception {
        doThrow(new CustomerNotFoundException(99L)).when(customerService).deleteCustomer(99L);

        mockMvc.perform(delete("/api/customers/99")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", containsString("99")));
    }
}
