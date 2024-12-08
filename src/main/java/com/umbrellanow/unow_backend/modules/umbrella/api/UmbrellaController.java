package com.umbrellanow.unow_backend.modules.umbrella.api;

import com.umbrellanow.unow_backend.modules.umbrella.api.model.CreateUmbrellaRequest;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.GetUmbrellaByGroupNameRequest;
import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaService;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/umbrella")
public class UmbrellaController {
    private final UmbrellaService umbrellaService;

    @Autowired
    public UmbrellaController(UmbrellaService umbrellaService) {
        this.umbrellaService = umbrellaService;
    }


    @GetMapping("/umbrellas")
    public ResponseEntity<?> getAllUmbrellasByUmbrellaGroup(@RequestBody GetUmbrellaByGroupNameRequest request) {
        List<Umbrella> umbrellasByGroupName = umbrellaService.getUmbrellasByGroupName(request.getGroupName());
        return ResponseEntity.ok(umbrellasByGroupName);
    }

    @PostMapping("/create-umbrella")
    public ResponseEntity<?> createUmbrellaByGroupName(@RequestBody CreateUmbrellaRequest request) {
        umbrellaService.createUmbrella(request.getStorageID(), request.getUmbrellaGroupName());
        return ResponseEntity.ok("");
    }
}
