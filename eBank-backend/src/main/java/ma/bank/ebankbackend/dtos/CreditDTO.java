package ma.bank.ebankbackend.dtos;

import lombok.Data;

@Data
public class CreditDTO {
    private String accountId;
    private Double amount;
    private String description;
}
