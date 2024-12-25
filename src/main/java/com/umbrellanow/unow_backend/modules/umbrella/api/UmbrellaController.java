package com.umbrellanow.unow_backend.modules.umbrella.api;

import com.umbrellanow.unow_backend.modules.rate.infrastructure.entity.PriceRate;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.CreateUmbrellaRequest;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.GetUmbrellaByGroupNameRequest;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.GetUmbrellaByIDRequest;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.UmbrellaPriceRateResponse;
import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaService;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
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

    @GetMapping("/umbrella")
    public ResponseEntity<?> getUmbrellaDailyRate(@RequestBody GetUmbrellaByIDRequest request) {
        return ResponseEntity.ok(umbrellaService.getUmbrellaByID(request.getId()));
    }

    @GetMapping("/umbrella-price-rate")
    public ResponseEntity<UmbrellaPriceRateResponse> getUmbrellaPriceRate(@QueryParam("id") String umbrellaID) {
        PriceRate priceRateForUmbrella = umbrellaService.getPriceRateForUmbrella(umbrellaID);
        UmbrellaPriceRateResponse umbrellaPriceRateResponse = new UmbrellaPriceRateResponse();
        umbrellaPriceRateResponse.setDailyRate(priceRateForUmbrella.getDailyRate());
        umbrellaPriceRateResponse.setHourlyRate(priceRateForUmbrella.getHourlyRate());
        umbrellaPriceRateResponse.setDeposit(priceRateForUmbrella.getDeposit());
        return ResponseEntity.ok(umbrellaPriceRateResponse);
    }
}
