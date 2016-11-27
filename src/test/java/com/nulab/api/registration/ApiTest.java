package com.nulab.api.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulab.api.Api;
import com.nulab.api.HtmlServer;
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
@PowerMockIgnore({"javax.management.*"})
public class ApiTest {

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private Api registeration;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(registeration).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTicket() throws Exception {
        NewSupportRegistration newSupportRegistration = new NewSupportRegistration("a", "a@a.com", "a");
        mockMvc.perform(post("/register/ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content((new ObjectMapper().writeValueAsString(newSupportRegistration)).getBytes()))
                .andExpect(status().isOk());
        newSupportRegistration = new NewSupportRegistration("Mayank ", "red", "a");
    }

    @Test
    public void testSendToSupport() throws Exception {
        NewSupportRegistration newSupportRegistration = new NewSupportRegistration("a", "a@a.com", "a");
        mockMvc.perform(post("/messages/1/asdasd")
                .contentType(MediaType.APPLICATION_JSON)
                .content((new ObjectMapper().writeValueAsString(newSupportRegistration)).getBytes()))
                .andExpect(status().isOk());
    }

    @Test
    public void testSendMessageToSupport() throws Exception {
        NewSupportRegistration newSupportRegistration = new NewSupportRegistration("a", "a@a.com", "a");
        mockMvc.perform(post("/message/1/asdasds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(("Hello").getBytes()))
                .andExpect(status().isOk());
    }


}
