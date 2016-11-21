package com.nulab.api.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulab.data.pojo.NewSupportRegistration;
import com.nulab.data.service.ApplicationService;
import com.nulab.data.util.ValidationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Simple Tests to make sure API serialization/deserialization works
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"})
public class RegisterationTest {

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private Registeration registeration;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(registeration).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTicket() throws Exception{
        NewSupportRegistration newSupportRegistration = new NewSupportRegistration("a","a@a.com","a");
        mockMvc.perform(post("/register/ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content((new ObjectMapper().writeValueAsString(newSupportRegistration)).getBytes()))
                .andExpect(status().isOk());
    }


}