package ma.bank.ebankbackend.dtos;

import jakarta.persistence.*;

import lombok.Data;
import ma.bank.ebankbackend.enums.AccountStatus;

import java.util.Date;


@Data
public class SavingBankAccountDTO extends BankAccountDTO{
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double interestRate;
}
