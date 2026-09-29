package ma.bank.ebankbackend.web;

import ma.bank.ebankbackend.dtos.CustomerDTO;
import ma.bank.ebankbackend.services.BankAccountService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

public class CustomerRestControllerTest {
    @Mock
    BankAccountService bankAccountService;
    @InjectMocks
    CustomerRestController customerRestController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCustomers() throws Exception {
        when(bankAccountService.ListCustomers()).thenReturn(List.of(new CustomerDTO()));

        List<CustomerDTO> result = customerRestController.customers();
        Assert.assertEquals(List.of(new CustomerDTO()), result);
    }

    @Test
    public void testSearchCustomers() throws Exception {
        when(bankAccountService.searchCustomers(anyString())).thenReturn(List.of(new CustomerDTO()));

        List<CustomerDTO> result = customerRestController.searchCustomers("keyword");
        Assert.assertEquals(List.of(new CustomerDTO()), result);
    }

    @Test
    public void testGetCustomerById() throws Exception {
        when(bankAccountService.getCustomer(anyLong())).thenReturn(new CustomerDTO());

        CustomerDTO result = customerRestController.getCustomerById(Long.valueOf(1));
        Assert.assertEquals(new CustomerDTO(), result);
    }

    @Test
    public void testSaveCustomer() throws Exception {
        when(bankAccountService.saveCustomer(any(CustomerDTO.class))).thenReturn(new CustomerDTO());

        CustomerDTO result = customerRestController.saveCustomer(new CustomerDTO());
        Assert.assertEquals(new CustomerDTO(), result);
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        when(bankAccountService.updateCustomer(any(CustomerDTO.class))).thenReturn(new CustomerDTO());

        CustomerDTO result = customerRestController.updateCustomer(Long.valueOf(1), new CustomerDTO());
        Assert.assertEquals(new CustomerDTO(), result);
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        customerRestController.deleteCustomer(Long.valueOf(1));
        verify(bankAccountService).deleteCustomer(anyLong());
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme