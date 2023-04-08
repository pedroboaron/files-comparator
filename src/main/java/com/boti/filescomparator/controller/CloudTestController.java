package com.boti.filescomparator.controller;

import com.boti.filescomparator.service.CloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/cloud")
@RequiredArgsConstructor
@Slf4j
public class CloudTestController {

    private final CloudService service;
    @GetMapping()
    public ResponseEntity<String> login() throws IOException {
        return ResponseEntity.ok().body(service.login());
    }
}
