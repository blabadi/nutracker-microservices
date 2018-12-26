package com.basharllabadi.nutracker.configserver;

import com.basharallabadi.nutracker.configserver.ConfigServerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("native")
public class ContextTest {

    @Autowired
    WebApplicationContext spring;
    MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(spring).build();
    }

    @Test
    public void propertyLoadTest() throws Exception {
        mockMvc.perform(get("/test-app/default"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }
}
