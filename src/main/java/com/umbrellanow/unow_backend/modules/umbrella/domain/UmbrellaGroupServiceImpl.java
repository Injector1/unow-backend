package com.umbrellanow.unow_backend.modules.umbrella.domain;

import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.UmbrellaGroupRepository;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.UmbrellaGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmbrellaGroupServiceImpl implements UmbrellaGroupService {
    private final UmbrellaGroupRepository umbrellaGroupRepository;


    @Autowired
    public UmbrellaGroupServiceImpl(UmbrellaGroupRepository umbrellaGroupRepository) {
        this.umbrellaGroupRepository = umbrellaGroupRepository;
    }


    @Override
    public List<UmbrellaGroup> getAllUmbrellaGroups() {
        return umbrellaGroupRepository.findAll();
    }
}
