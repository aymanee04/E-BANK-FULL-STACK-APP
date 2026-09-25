package ma.bank.ebankbackend.repositories;

import ma.bank.ebankbackend.entities.BankAccount;
import ma.bank.ebankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {

}
