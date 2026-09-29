package ma.bank.ebankbackend.web;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
//import static org.mockito.Mockito.*;

public class CustomerRestControllerTest {
    @Mock
    ma.bank.ebankbackend.services.BankAccountService bankAccountService;
    @InjectMocks
    ma.bank.ebankbackend.web.CustomerRestController customerRestController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCustomers() throws Exception {
        when(bankAccountService.ListCustomers()).thenReturn(java.util.List.of(new ma.bank.ebankbackend.dtos.CustomerDTO()));

        java.util.List<ma.bank.ebankbackend.dtos.CustomerDTO> result = customerRestController.customers();
        Assert.assertEquals(java.util.List.of(new ma.bank.ebankbackend.dtos.CustomerDTO()), result);
    }

    @Test
    public void testSearchCustomers() throws Exception {
        when(bankAccountService.searchCustomers(anyString())).thenReturn(java.util.List.of(new ma.bank.ebankbackend.dtos.CustomerDTO()));

        java.util.List<ma.bank.ebankbackend.dtos.CustomerDTO> result = customerRestController.searchCustomers("keyword");
        Assert.assertEquals(java.util.List.of(new ma.bank.ebankbackend.dtos.CustomerDTO()), result);
    }

    @Test
    public void testGetCustomerById() throws Exception {
        when(bankAccountService.getCustomer(anyLong())).thenReturn(new ma.bank.ebankbackend.dtos.CustomerDTO());

        ma.bank.ebankbackend.dtos.CustomerDTO result = customerRestController.getCustomerById(Long.valueOf(1));
        Assert.assertEquals(new ma.bank.ebankbackend.dtos.CustomerDTO(), result);
    }

    @Test
    public void testSaveCustomer() throws Exception {
        when(bankAccountService.saveCustomer(any(ma.bank.ebankbackend.dtos.CustomerDTO.class))).thenReturn(new ma.bank.ebankbackend.dtos.CustomerDTO());

        ma.bank.ebankbackend.dtos.CustomerDTO result = customerRestController.saveCustomer(new ma.bank.ebankbackend.dtos.CustomerDTO());
        Assert.assertEquals(new ma.bank.ebankbackend.dtos.CustomerDTO(), result);
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        when(bankAccountService.updateCustomer(any(ma.bank.ebankbackend.dtos.CustomerDTO.class))).thenReturn(new ma.bank.ebankbackend.dtos.CustomerDTO());

        ma.bank.ebankbackend.dtos.CustomerDTO result = customerRestController.updateCustomer(Long.valueOf(1), new ma.bank.ebankbackend.dtos.CustomerDTO());
        Assert.assertEquals(new ma.bank.ebankbackend.dtos.CustomerDTO(), result);
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        customerRestController.deleteCustomer(Long.valueOf(1));
        verify(bankAccountService).deleteCustomer(anyLong());
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme