package com.umbrellanow.unow_backend.modules.umbrella.api;

import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/umbrella-group")
public class UmbrellaGroupController {
    private final UmbrellaGroupService umbrellaGroupService;

    @Autowired
    public UmbrellaGroupController(UmbrellaGroupService umbrellaGroupService) {
        this.umbrellaGroupService = umbrellaGroupService;
    }


    @GetMapping("/umbrella-groups")
    public ResponseEntity<?> getAllUmbrellaGroups() {
        return ResponseEntity.ok(umbrellaGroupService.getAllUmbrellaGroups());
    }
}
