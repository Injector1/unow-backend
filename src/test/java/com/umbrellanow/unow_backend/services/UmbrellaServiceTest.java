package com.umbrellanow.unow_backend.services;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.StorageBoxRepository;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaService;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.UmbrellaGroupRepository;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.UmbrellaGroup;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UmbrellaServiceTest {
    @Autowired
    private UmbrellaService umbrellaService;
    @Autowired
    private StorageBoxRepository storageBoxRepository;
    @Autowired
    private UmbrellaGroupRepository umbrellaGroupRepository;

    @Test
    public void testCreateUmbrellaFail() {
        StorageBox sb = storageBoxRepository.findAll().getFirst();
        UmbrellaGroup ug = umbrellaGroupRepository.findAll().getFirst();

        List<Umbrella> umbrellasByGroupNameBefore = umbrellaService.getUmbrellasByGroupName(ug.getName());

        assertEquals(1, umbrellasByGroupNameBefore.size());
        assertThrows(IllegalArgumentException.class, () -> umbrellaService.createUmbrella(sb.getId(), ug.getName()));
    }

    @Test
    public void testCreateUmbrella() {
        StorageBox sbNew = new StorageBox();
        sbNew.setIsEmpty(true);
        sbNew.setCode(1111);
        sbNew.setNumber(123);
        StorageBox savedSB = storageBoxRepository.save(sbNew);
        UmbrellaGroup ug = umbrellaGroupRepository.findAll().getFirst();

        Umbrella umbrella = umbrellaService.createUmbrella(savedSB.getId(), ug.getName());

        Umbrella existingUmbrella = umbrellaService.getUmbrellaByID(umbrella.getId().toString());

        assertNotNull(existingUmbrella);
        assertEquals(sbNew.getCode(), existingUmbrella.getStorageBox().getCode());
        assertEquals("umbrella/" + existingUmbrella.getId() + "/", existingUmbrella.getS3Path());
    }
}
