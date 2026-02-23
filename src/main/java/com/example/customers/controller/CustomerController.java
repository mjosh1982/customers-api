package com.example.customers.controller;

import com.example.customers.model.Customer;
import com.example.customers.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Operations related to customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(
        summary = "Get all customers",
        description = "Returns a list of all customers in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved list of customers",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))
    )
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by ID", description = "Returns a single customer by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found",
            content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    @Operation(
        summary = "Add a customer",
        description = "Creates a new customer and returns the saved record"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = Customer.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.addCustomer(customer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer", description = "Updates an existing customer by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
            content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @Valid @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer", description = "Deletes a customer by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
