package ma.bank.ebankbackend.services;

import ma.bank.ebankbackend.dtos.*;
import ma.bank.ebankbackend.entities.*;
import ma.bank.ebankbackend.enums.OperationType;
import ma.bank.ebankbackend.exceptions.BalanceNotSufficientException;
import ma.bank.ebankbackend.exceptions.BankAccountNotFoundException;
import ma.bank.ebankbackend.exceptions.CustomerNotFoundException;
import ma.bank.ebankbackend.mappers.BankAccountMapperImpl;
import ma.bank.ebankbackend.repositories.AccountOperationRepository;
import ma.bank.ebankbackend.repositories.BankAccountRepository;
import ma.bank.ebankbackend.repositories.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private AccountOperationRepository accountOperationRepository;

    @Mock
    private BankAccountMapperImpl dtoMapper;

    @InjectMocks
    private BankAccountServiceImpl service;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = mock(Customer.class);
        customerDTO = mock(CustomerDTO.class);
    }

    // ============================================================
    // saveCustomer
    // ============================================================

    @Test
    void saveCustomer_shouldSaveAndReturnDto() {
        Customer savedCustomer = mock(Customer.class);
        CustomerDTO expectedDTO = mock(CustomerDTO.class);

        when(dtoMapper.fromCustomerDTO(customerDTO))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(savedCustomer);

        when(dtoMapper.fromCustomer(savedCustomer))
                .thenReturn(expectedDTO);

        CustomerDTO result = service.saveCustomer(customerDTO);

        assertSame(expectedDTO, result);

        verify(dtoMapper).fromCustomerDTO(customerDTO);
        verify(customerRepository).save(customer);
        verify(dtoMapper).fromCustomer(savedCustomer);
    }

    // ============================================================
    // saveCurrentBankAccount
    // ============================================================

    @Test
    void saveCurrentBankAccount_shouldCreateAndSaveCurrentAccount()
            throws CustomerNotFoundException {

        CurrentAccount savedAccount = mock(CurrentAccount.class);
        CurrentBankAccountDTO expectedDTO = mock(CurrentBankAccountDTO.class);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(bankAccountRepository.save(any(CurrentAccount.class)))
                .thenReturn(savedAccount);

        when(dtoMapper.fromCurrentAccount(savedAccount))
                .thenReturn(expectedDTO);

        CurrentBankAccountDTO result =
                service.saveCurrentBankAccount(1000.0, 500.0, 1L);

        assertSame(expectedDTO, result);

        ArgumentCaptor<CurrentAccount> captor =
                ArgumentCaptor.forClass(CurrentAccount.class);

        verify(bankAccountRepository).save(captor.capture());

        CurrentAccount account = captor.getValue();

        assertNotNull(account.getId());
        assertNotNull(account.getCreatedAt());
        assertEquals(1000.0, account.getBalance());
        assertEquals(500.0, account.getOverDraft());
        assertSame(customer, account.getCustomer());

        verify(customerRepository).findById(1L);
        verify(dtoMapper).fromCurrentAccount(savedAccount);
    }

    @Test
    void saveCurrentBankAccount_shouldThrowWhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> service.saveCurrentBankAccount(1000.0, 500.0, 1L)
        );

        verify(customerRepository).findById(1L);
        verifyNoInteractions(bankAccountRepository);
        verifyNoInteractions(dtoMapper);
    }

    // ============================================================
    // saveSavingBankAccount
    // ============================================================

    @Test
    void saveSavingBankAccount_shouldCreateAndSaveSavingAccount()
            throws CustomerNotFoundException {

        SavingAccount savedAccount = mock(SavingAccount.class);
        SavingBankAccountDTO expectedDTO = mock(SavingBankAccountDTO.class);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(bankAccountRepository.save(any(SavingAccount.class)))
                .thenReturn(savedAccount);

        when(dtoMapper.fromSavingAccount(savedAccount))
                .thenReturn(expectedDTO);

        SavingBankAccountDTO result =
                service.saveSavingBankAccount(2000.0, 2.5, 1L);

        assertSame(expectedDTO, result);

        ArgumentCaptor<SavingAccount> captor =
                ArgumentCaptor.forClass(SavingAccount.class);

        verify(bankAccountRepository).save(captor.capture());

        SavingAccount account = captor.getValue();

        assertNotNull(account.getId());
        assertNotNull(account.getCreatedAt());
        assertEquals(2000.0, account.getBalance());
        assertEquals(2.5, account.getInterstRate());
        assertSame(customer, account.getCustomer());

        verify(dtoMapper).fromSavingAccount(savedAccount);
    }

    @Test
    void saveSavingBankAccount_shouldThrowWhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> service.saveSavingBankAccount(2000.0, 2.5, 1L)
        );

        verify(customerRepository).findById(1L);
        verifyNoInteractions(bankAccountRepository);
        verifyNoInteractions(dtoMapper);
    }

    // ============================================================
    // ListCustomers
    // ============================================================

    @Test
    void listCustomers_shouldReturnMappedCustomers() {

        Customer customer1 = mock(Customer.class);
        Customer customer2 = mock(Customer.class);

        CustomerDTO dto1 = mock(CustomerDTO.class);
        CustomerDTO dto2 = mock(CustomerDTO.class);

        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));

        when(dtoMapper.fromCustomer(customer1))
                .thenReturn(dto1);

        when(dtoMapper.fromCustomer(customer2))
                .thenReturn(dto2);

        List<CustomerDTO> result = service.ListCustomers();

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        verify(dtoMapper).fromCustomer(customer1);
        verify(dtoMapper).fromCustomer(customer2);
    }

    @Test
    void listCustomers_shouldReturnEmptyListWhenNoCustomers() {

        when(customerRepository.findAll())
                .thenReturn(List.of());

        List<CustomerDTO> result = service.ListCustomers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(customerRepository).findAll();
        verifyNoInteractions(dtoMapper);
    }

    // ============================================================
    // getBankAccount
    // ============================================================

    @Test
    void getBankAccount_shouldReturnSavingAccountDto()
            throws BankAccountNotFoundException {

        SavingAccount account = mock(SavingAccount.class);
        SavingBankAccountDTO expectedDTO = mock(SavingBankAccountDTO.class);

        when(bankAccountRepository.findById("saving-1"))
                .thenReturn(Optional.of(account));

        when(dtoMapper.fromSavingAccount(account))
                .thenReturn(expectedDTO);

        BankAccountDTO result =
                service.getBankAccount("saving-1");

        assertSame(expectedDTO, result);

        verify(dtoMapper).fromSavingAccount(account);
        verify(dtoMapper, never()).fromCurrentAccount((CurrentAccount) any());
    }

    @Test
    void getBankAccount_shouldReturnCurrentAccountDto()
            throws BankAccountNotFoundException {

        CurrentAccount account = mock(CurrentAccount.class);
        CurrentBankAccountDTO expectedDTO = mock(CurrentBankAccountDTO.class);

        when(bankAccountRepository.findById("current-1"))
                .thenReturn(Optional.of(account));

        when(dtoMapper.fromCurrentAccount(account))
                .thenReturn(expectedDTO);

        BankAccountDTO result =
                service.getBankAccount("current-1");

        assertSame(expectedDTO, result);

        verify(dtoMapper).fromCurrentAccount(account);
        verify(dtoMapper, never()).fromSavingAccount(any());
    }

    @Test
    void getBankAccount_shouldThrowWhenAccountDoesNotExist() {

        when(bankAccountRepository.findById("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> service.getBankAccount("unknown")
        );

        verify(bankAccountRepository).findById("unknown");
        verifyNoInteractions(dtoMapper);
    }

    // ============================================================
    // debit
    // ============================================================

    @Test
    void debit_shouldCreateDebitOperationAndUpdateBalance()
            throws Exception {

        BankAccount account = mock(BankAccount.class);

        when(bankAccountRepository.findById("acc-1"))
                .thenReturn(Optional.of(account));

        when(account.getBalance())
                .thenReturn(1000.0);

        service.debit("acc-1", 250.0, "ATM withdrawal");

        ArgumentCaptor<AccountOperation> operationCaptor =
                ArgumentCaptor.forClass(AccountOperation.class);

        verify(accountOperationRepository)
                .save(operationCaptor.capture());

        AccountOperation operation = operationCaptor.getValue();

        assertEquals(OperationType.DEBIT, operation.getType());
        assertEquals(250.0, operation.getAmount());
        assertEquals("ATM withdrawal", operation.getDescription());
        assertNotNull(operation.getOperationDate());
        assertSame(account, operation.getBankAccount());

        verify(account).setBalance(750.0);
        verify(bankAccountRepository).save(account);
    }

    @Test
    void debit_shouldThrowWhenAccountDoesNotExist() {

        when(bankAccountRepository.findById("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> service.debit("unknown", 100.0, "test")
        );

        verifyNoInteractions(accountOperationRepository);
    }

    @Test
    void debit_shouldThrowWhenBalanceIsInsufficient() {

        BankAccount account = mock(BankAccount.class);

        when(bankAccountRepository.findById("acc-1"))
                .thenReturn(Optional.of(account));

        when(account.getBalance())
                .thenReturn(50.0);

        assertThrows(
                BalanceNotSufficientException.class,
                () -> service.debit("acc-1", 100.0, "test")
        );

        verify(account, never()).setBalance(anyDouble());
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(accountOperationRepository);
    }

    // ============================================================
    // credit
    // ============================================================

    @Test
    void credit_shouldCreateCreditOperationAndUpdateBalance()
            throws Exception {

        BankAccount account = mock(BankAccount.class);

        when(bankAccountRepository.findById("acc-1"))
                .thenReturn(Optional.of(account));

        when(account.getBalance())
                .thenReturn(1000.0);

        service.credit("acc-1", 250.0, "Salary");

        ArgumentCaptor<AccountOperation> operationCaptor =
                ArgumentCaptor.forClass(AccountOperation.class);

        verify(accountOperationRepository)
                .save(operationCaptor.capture());

        AccountOperation operation = operationCaptor.getValue();

        assertEquals(OperationType.CREDIT, operation.getType());
        assertEquals(250.0, operation.getAmount());
        assertEquals("Salary", operation.getDescription());
        assertNotNull(operation.getOperationDate());
        assertSame(account, operation.getBankAccount());

        verify(account).setBalance(1250.0);
        verify(bankAccountRepository).save(account);
    }

    @Test
    void credit_shouldThrowWhenAccountDoesNotExist() {

        when(bankAccountRepository.findById("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> service.credit("unknown", 100.0, "test")
        );

        verifyNoInteractions(accountOperationRepository);
    }

    // ============================================================
    // transfer
    // ============================================================

    @Test
    void transfer_shouldDebitSourceAndCreditDestination()
            throws Exception {

        BankAccount source = mock(BankAccount.class);
        BankAccount destination = mock(BankAccount.class);

        when(bankAccountRepository.findById("source"))
                .thenReturn(Optional.of(source));

        when(bankAccountRepository.findById("destination"))
                .thenReturn(Optional.of(destination));

        when(source.getBalance())
                .thenReturn(1000.0);

        when(destination.getBalance())
                .thenReturn(500.0);

        service.transfer("source", "destination", 300.0);

        verify(source).setBalance(700.0);
        verify(destination).setBalance(800.0);

        verify(bankAccountRepository).save(source);
        verify(bankAccountRepository).save(destination);

        verify(accountOperationRepository, times(2))
                .save(any(AccountOperation.class));
    }

    @Test
    void transfer_shouldThrowWhenSourceDoesNotExist() {

        when(bankAccountRepository.findById("source"))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> service.transfer(
                        "source",
                        "destination",
                        100.0
                )
        );

        verify(accountOperationRepository, never())
                .save(any());

        verify(bankAccountRepository, never())
                .save(any());
    }

    @Test
    void transfer_shouldThrowWhenSourceBalanceIsInsufficient() {

        BankAccount source = mock(BankAccount.class);

        when(bankAccountRepository.findById("source"))
                .thenReturn(Optional.of(source));

        when(source.getBalance())
                .thenReturn(50.0);

        assertThrows(
                BalanceNotSufficientException.class,
                () -> service.transfer(
                        "source",
                        "destination",
                        100.0
                )
        );

        verify(bankAccountRepository, never()).save(any());
        verify(accountOperationRepository, never()).save(any());
    }

    // ============================================================
    // bankAccountList
    // ============================================================

    @Test
    void bankAccountList_shouldMapSavingAndCurrentAccounts() {

        SavingAccount savingAccount = mock(SavingAccount.class);
        CurrentAccount currentAccount = mock(CurrentAccount.class);

        SavingBankAccountDTO savingDTO =
                mock(SavingBankAccountDTO.class);

        CurrentBankAccountDTO currentDTO =
                mock(CurrentBankAccountDTO.class);

        when(bankAccountRepository.findAll())
                .thenReturn(List.of(savingAccount, currentAccount));

        when(dtoMapper.fromSavingAccount(savingAccount))
                .thenReturn(savingDTO);

        when(dtoMapper.fromCurrentAccount(currentAccount))
                .thenReturn(currentDTO);

        List<BankAccountDTO> result =
                service.bankAccountList();

        assertEquals(2, result.size());
        assertSame(savingDTO, result.get(0));
        assertSame(currentDTO, result.get(1));

        verify(dtoMapper).fromSavingAccount(savingAccount);
        verify(dtoMapper).fromCurrentAccount(currentAccount);
    }

    @Test
    void bankAccountList_shouldReturnEmptyListWhenNoAccounts() {

        when(bankAccountRepository.findAll())
                .thenReturn(List.of());

        List<BankAccountDTO> result =
                service.bankAccountList();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(dtoMapper);
    }

    // ============================================================
    // getCustomer
    // ============================================================

    @Test
    void getCustomer_shouldReturnCustomerDto()
            throws CustomerNotFoundException {

        CustomerDTO expectedDTO = mock(CustomerDTO.class);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(dtoMapper.fromCustomer(customer))
                .thenReturn(expectedDTO);

        CustomerDTO result =
                service.getCustomer(1L);

        assertSame(expectedDTO, result);

        verify(dtoMapper).fromCustomer(customer);
    }

    @Test
    void getCustomer_shouldThrowWhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> service.getCustomer(1L)
        );

        verifyNoInteractions(dtoMapper);
    }

    // ============================================================
    // updateCustomer
    // ============================================================

    @Test
    void updateCustomer_shouldUpdateAndReturnDto() {

        Customer savedCustomer = mock(Customer.class);
        CustomerDTO expectedDTO = mock(CustomerDTO.class);

        when(dtoMapper.fromCustomerDTO(customerDTO))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(savedCustomer);

        when(dtoMapper.fromCustomer(savedCustomer))
                .thenReturn(expectedDTO);

        CustomerDTO result =
                service.updateCustomer(customerDTO);

        assertSame(expectedDTO, result);

        verify(dtoMapper).fromCustomerDTO(customerDTO);
        verify(customerRepository).save(customer);
        verify(dtoMapper).fromCustomer(savedCustomer);
    }

    // ============================================================
    // deleteCustomer
    // ============================================================

    @Test
    void deleteCustomer_shouldDeleteCustomer() {

        service.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }

    // ============================================================
    // accountHistory
    // ============================================================

    @Test
    void accountHistory_shouldReturnMappedOperations() {

        AccountOperation operation1 =
                mock(AccountOperation.class);

        AccountOperation operation2 =
                mock(AccountOperation.class);

        AccountOperationDTO dto1 =
                mock(AccountOperationDTO.class);

        AccountOperationDTO dto2 =
                mock(AccountOperationDTO.class);

        when(accountOperationRepository
                .findByBankAccountId("acc-1"))
                .thenReturn(List.of(operation1, operation2));

        when(dtoMapper.fromAccountOperation(operation1))
                .thenReturn(dto1);

        when(dtoMapper.fromAccountOperation(operation2))
                .thenReturn(dto2);

        List<AccountOperationDTO> result =
                service.accountHistory("acc-1");

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        verify(dtoMapper).fromAccountOperation(operation1);
        verify(dtoMapper).fromAccountOperation(operation2);
    }

    @Test
    void accountHistory_shouldReturnEmptyListWhenNoOperations() {

        when(accountOperationRepository
                .findByBankAccountId("acc-1"))
                .thenReturn(List.of());

        List<AccountOperationDTO> result =
                service.accountHistory("acc-1");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(dtoMapper);
    }

    // ============================================================
    // getAccountHistory
    // ============================================================

    @Test
    void getAccountHistory_shouldReturnHistory()
            throws BankAccountNotFoundException {

        BankAccount account = mock(BankAccount.class);

        AccountOperation operation =
                mock(AccountOperation.class);

        AccountOperationDTO operationDTO =
                mock(AccountOperationDTO.class);

        Page<AccountOperation> page =
                mock(Page.class);

        when(bankAccountRepository.findById("acc-1"))
                .thenReturn(Optional.of(account));

        when(account.getId())
                .thenReturn("acc-1");

        when(account.getBalance())
                .thenReturn(1500.0);

        when(accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(
                        eq("acc-1"),
                        eq(PageRequest.of(1, 10))))
                .thenReturn(page);

        when(page.getContent())
                .thenReturn(List.of(operation));

        when(page.getTotalPages())
                .thenReturn(3);

        when(dtoMapper.fromAccountOperation(operation))
                .thenReturn(operationDTO);

        AccountHistoryDTO result =
                service.getAccountHistory("acc-1", 1, 10);

        assertNotNull(result);
        assertEquals("acc-1", result.getAccountId());
        assertEquals(1500.0, result.getBalance());
        assertEquals(1, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(3, result.getTotalPages());

        assertEquals(
                List.of(operationDTO),
                result.getAccountOperationDTOS()
        );

        verify(accountOperationRepository)
                .findByBankAccountIdOrderByOperationDateDesc(
                        "acc-1",
                        PageRequest.of(1, 10)
                );
    }

    @Test
    void getAccountHistory_shouldThrowWhenAccountDoesNotExist() {

        when(bankAccountRepository.findById("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> service.getAccountHistory(
                        "unknown",
                        0,
                        10
                )
        );

        verify(
                accountOperationRepository,
                never()
        ).findByBankAccountIdOrderByOperationDateDesc(
                anyString(),
                any(PageRequest.class)
        );
    }

    // ============================================================
    // searchCustomers
    // ============================================================

    @Test
    void searchCustomers_shouldReturnMatchingCustomers() {

        Customer customer1 = mock(Customer.class);
        Customer customer2 = mock(Customer.class);

        CustomerDTO dto1 = mock(CustomerDTO.class);
        CustomerDTO dto2 = mock(CustomerDTO.class);

        when(customerRepository.findByNameContains("aym"))
                .thenReturn(List.of(customer1, customer2));

        when(dtoMapper.fromCustomer(customer1))
                .thenReturn(dto1);

        when(dtoMapper.fromCustomer(customer2))
                .thenReturn(dto2);

        List<CustomerDTO> result =
                service.searchCustomers("aym");

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        verify(customerRepository)
                .findByNameContains("aym");

        verify(dtoMapper).fromCustomer(customer1);
        verify(dtoMapper).fromCustomer(customer2);
    }

    @Test
    void searchCustomers_shouldReturnEmptyListWhenNothingFound() {

        when(customerRepository.findByNameContains("unknown"))
                .thenReturn(List.of());

        List<CustomerDTO> result =
                service.searchCustomers("unknown");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(dtoMapper);
    }
}