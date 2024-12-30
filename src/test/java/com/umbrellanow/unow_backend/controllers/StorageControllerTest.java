package com.umbrellanow.unow_backend.controllers;

import com.umbrellanow.unow_backend.modules.storage.api.StorageController;
import com.umbrellanow.unow_backend.modules.storage.domain.StorageService;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class StorageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testEmptyBoxFound() throws Exception {
        StorageBox sb = new StorageBox();
        sb.setIsEmpty(true);
        sb.setCode(1111);
        sb.setId(123L);
        sb.setNumber(8);

        StorageService storageService = Mockito.mock(StorageService.class);
        Mockito.when(storageService.findEmptyStorageBox()).thenReturn(sb);

        StorageController storageController = new StorageController(storageService);

        mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/storage/empty-box")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(sb.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.boxNumber").value(sb.getNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.boxCode").value(sb.getCode()));
    }

    @Test
    public void testEmptyBoxNotFound() throws Exception {
        StorageService storageService = Mockito.mock(StorageService.class);
        Mockito.when(storageService.findEmptyStorageBox()).thenReturn(null);

        StorageController storageController = new StorageController(storageService);

        mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/storage/empty-box")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
