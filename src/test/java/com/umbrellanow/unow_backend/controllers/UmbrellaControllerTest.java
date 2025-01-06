package com.umbrellanow.unow_backend.controllers;

import com.umbrellanow.unow_backend.integrations.s3.S3Service;
import com.umbrellanow.unow_backend.modules.umbrella.api.UmbrellaController;
import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaService;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UmbrellaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadPhotoSuccess() throws Exception {
        Umbrella mockUmbrella = new Umbrella();
        mockUmbrella.setId(1L);
        mockUmbrella.setS3Path("s3/umbrella/1");

        UmbrellaService umbrellaService = Mockito.mock(UmbrellaService.class);
        S3Service storageService = Mockito.mock(S3Service.class);

        Mockito.when(umbrellaService.getUmbrellaByID(1L)).thenReturn(mockUmbrella);
        Mockito.doNothing().when(storageService).uploadFile(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(InputStream.class),
                Mockito.anyLong(),
                Mockito.anyString()
        );

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "umbrella.jpg",
                "image/jpeg",
                "Dummy content".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "id",
                "",
                "text/plain",
                "1".getBytes()
        );

        UmbrellaController umbrellaController = new UmbrellaController(umbrellaService, storageService);
        mockMvc = MockMvcBuilders.standaloneSetup(umbrellaController).build();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/umbrella/upload-photo")
                        .file(photo)
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Photo uploaded successfully."));
    }

    @Test
    public void testUploadPhotoUmbrellaNotFound() throws Exception {
        UmbrellaService umbrellaService = Mockito.spy(UmbrellaService.class);
        S3Service storageService = Mockito.mock(S3Service.class);

        Mockito.doReturn(null).when(umbrellaService).getUmbrellaByID(Mockito.anyLong());
        Mockito.doNothing().when(storageService).uploadFile(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(InputStream.class),
                Mockito.anyLong(),
                Mockito.anyString()
        );


        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "umbrella.jpg",
                "image/jpeg",
                "Dummy content".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "id",
                "",
                "text/plain",
                "1".getBytes()
        );

        UmbrellaController umbrellaController = new UmbrellaController(umbrellaService, storageService);
        mockMvc = MockMvcBuilders.standaloneSetup(umbrellaController).build();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/umbrella/upload-photo")
                        .file(photo)
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Umbrella with ID 1 not found."));
    }

    @Test
    public void testUploadPhotoInvalidImage() throws Exception {
        UmbrellaService umbrellaService = Mockito.mock(UmbrellaService.class);
        S3Service storageService = Mockito.mock(S3Service.class);

        Umbrella mockUmbrella = new Umbrella();
        mockUmbrella.setId(1L);
        mockUmbrella.setS3Path("s3/umbrella/1");

        Mockito.when(umbrellaService.getUmbrellaByID(1L)).thenReturn(mockUmbrella);

        MockMultipartFile invalidPhoto = new MockMultipartFile(
                "photo",
                "umbrella.txt",
                "text/plain",
                "Dummy content".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "id",
                "",
                "text/plain",
                "1".getBytes()
        );

        UmbrellaController umbrellaController = new UmbrellaController(umbrellaService, storageService);
        mockMvc = MockMvcBuilders.standaloneSetup(umbrellaController).build();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/umbrella/upload-photo")
                        .file(invalidPhoto)
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Uploaded file is not a valid image."));
    }
}

