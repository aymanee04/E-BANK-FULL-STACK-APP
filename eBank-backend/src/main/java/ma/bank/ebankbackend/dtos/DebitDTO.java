package ma.bank.ebankbackend.dtos;

import lombok.Data;

@Data
public class DebitDTO {
    private String accountId;
    private Double amount;
    private String description;
}
