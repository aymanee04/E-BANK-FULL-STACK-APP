package ma.bank.ebankbackend;

import ma.bank.ebankbackend.dtos.BankAccountDTO;
import ma.bank.ebankbackend.dtos.CurrentBankAccountDTO;
import ma.bank.ebankbackend.dtos.CustomerDTO;
import ma.bank.ebankbackend.dtos.SavingBankAccountDTO;
import ma.bank.ebankbackend.entities.*;
import ma.bank.ebankbackend.enums.AccountStatus;
import ma.bank.ebankbackend.enums.OperationType;
import ma.bank.ebankbackend.exceptions.BalanceNotSufficientException;
import ma.bank.ebankbackend.exceptions.BankAccountNotFoundException;
import ma.bank.ebankbackend.exceptions.CustomerNotFoundException;
import ma.bank.ebankbackend.repositories.AccountOperationRepository;
import ma.bank.ebankbackend.repositories.BankAccountRepository;
import ma.bank.ebankbackend.repositories.CustomerRepository;
import ma.bank.ebankbackend.services.BankAccountService;
import ma.bank.ebankbackend.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBankBackendApplication.class, args);
    }
//@Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
          Stream.of("Aymane","Mohamed","Hassan").forEach(name -> {
              CustomerDTO customer = new CustomerDTO();
              customer.setName(name);
              customer.setEmail(name +"@gmail.com");
              bankAccountService.saveCustomer(customer);
          });
          bankAccountService.ListCustomers().forEach(customer -> {
              try {
                  bankAccountService.saveCurrentBankAccount(Math.random()*90000,customer.getId(),1L);
                  bankAccountService.saveSavingBankAccount(Math.random()*90000,customer.getId(),5L);

              }
              catch (CustomerNotFoundException  e) {
                  e.printStackTrace();
              }
          });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i < 10; i++) {
                    String accountId;
                    if (bankAccount instanceof SavingBankAccountDTO){
                        accountId=((SavingBankAccountDTO)bankAccount).getId();
                    }else {
                        accountId=((CurrentBankAccountDTO)bankAccount).getId();
                    }
                    bankAccountService.credit(accountId, 1000+Math.random()*12000,"Credited successfully");
                    bankAccountService.debit(accountId, 1000+Math.random()*9000,"Debited Successfully");
                }

            }
        };
    }

    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository ,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Aymane","Yassine","Med").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*2000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*2000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(customer);
                savingAccount.setInterstRate(5.3);
                bankAccountRepository.save(savingAccount);
            });
            bankAccountRepository.findAll().forEach(acc ->{
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }
            });
        };
    }

}
