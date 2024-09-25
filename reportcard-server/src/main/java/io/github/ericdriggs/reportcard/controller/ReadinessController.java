package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.browse.BrowseUIController;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class ReadinessController {

    @Autowired
    BrowseUIController browseUIController;

    @Autowired
    StorageController storageController;



    @GetMapping(path = "/readiness")
    public ResponseEntity<String> healthCheck() {

        ResponseEntity<String> companiesResponse = browseUIController.getCompanies();
        ResponseEntity<?> storageResponse = storageController.getKeyForPrefix("rc/");

        Map<String,HttpStatus> readinessMap = new TreeMap<>();
        readinessMap.put("db", companiesResponse.getStatusCode());
        readinessMap.put("storage", storageResponse.getStatusCode());

        HttpStatus httpStatus = HttpStatus.OK;
        for (HttpStatus s : readinessMap.values()) {
            final int statusCode = (s == null) ? 500 : s.value();
            if (httpStatus.value() < statusCode) {
                httpStatus = s;
            }
        }

        return new ResponseEntity<>(SharedObjectMappers.writeValueAsString(readinessMap), httpStatus);
    }
}
