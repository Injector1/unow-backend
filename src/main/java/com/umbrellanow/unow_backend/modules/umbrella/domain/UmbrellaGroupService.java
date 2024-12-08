package com.umbrellanow.unow_backend.modules.umbrella.domain;

import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.UmbrellaGroup;

import java.util.List;

public interface UmbrellaGroupService {
    List<UmbrellaGroup> getAllUmbrellaGroups();
}
