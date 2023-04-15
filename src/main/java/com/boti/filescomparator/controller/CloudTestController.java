package com.boti.filescomparator.controller;

import com.boti.filescomparator.dto.ItemComparacao;
import com.boti.filescomparator.service.CloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/onedrive")
@RequiredArgsConstructor
@Slf4j
public class CloudTestController {

    private final CloudService service;

    @GetMapping()
    public ResponseEntity<String> login() throws Exception {
        return ResponseEntity.ok().body(service.login());
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List> getUsers() throws Exception {
        return ResponseEntity.ok().body(service.getUsers());
    }

    @GetMapping(value = "/DAS")
    public ResponseEntity compareDAS(@RequestParam("empresa") Integer empresa,
                                     @RequestParam("periodo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo
    ) throws Exception {

        return ResponseEntity.ok().body(service.compare(empresa, periodo));
    }

    @GetMapping(value = "/lerArquivo")
    public ResponseEntity<String> lerArquivo() throws Exception {
        return ResponseEntity.ok().body(service.getPdfFileByItemIdToString("01WDX6HQH3SVA665HQZNA2EZGXRYRDFMSK"));
    }
}
