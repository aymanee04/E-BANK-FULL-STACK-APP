package ma.bank.ebankbackend.services;

import ma.bank.ebankbackend.entities.BankAccount;
import ma.bank.ebankbackend.entities.Customer;
import ma.bank.ebankbackend.repositories.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

class BankServiceTest {

    @Test
    void consulter_shouldNotThrowWhenAccountExists() {
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        BankService bankService = new BankService(bankAccountRepository);

        BankAccount bankAccount = Mockito.mock(BankAccount.class);
        Customer customer = Mockito.mock(Customer.class);

        when(customer.getName()).thenReturn("John Doe");
        when(bankAccount.getId()).thenReturn("0b36be78-8d5d-446b-9f20-37eadc9d3c3b");
        when(bankAccount.getBalance()).thenReturn(1000.0);
        when(bankAccount.getStatus()).thenReturn(null);
        when(bankAccount.getCreatedAt()).thenReturn(null);
        when(bankAccount.getCustomer()).thenReturn(customer);
        when(bankAccount.getAccountOperations()).thenReturn(Collections.emptyList());

        when(bankAccountRepository.findById("0b36be78-8d5d-446b-9f20-37eadc9d3c3b"))
                .thenReturn(Optional.of(bankAccount));

        assertDoesNotThrow(bankService::consulter);
    }

    @Test
    void consulter_shouldNotThrowWhenAccountDoesNotExist() {
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        BankService bankService = new BankService(bankAccountRepository);

        when(bankAccountRepository.findById("0b36be78-8d5d-446b-9f20-37eadc9d3c3b"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(bankService::consulter);
    }
}