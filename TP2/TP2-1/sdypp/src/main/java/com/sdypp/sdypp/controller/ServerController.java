package com.sdypp.sdypp.controller;

import com.sdypp.sdypp.domain.FileLocation;
import com.sdypp.sdypp.dto.FileNameDto;
import com.sdypp.sdypp.dto.FileOwnerDto;
import com.sdypp.sdypp.dto.HelloDto;
import com.sdypp.sdypp.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/p2p")
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;

    @GetMapping("/all")
    public List<FileLocation> findAll() {
        return serverService.findAll();
    }

    @PostMapping("/hello")
    public void hello(@RequestBody @Validated HelloDto helloDto) {
        serverService.hello(helloDto);
    }

    @PostMapping("/locate")
    public List<FileOwnerDto> locate(@RequestBody @Validated FileNameDto fileNameDto) {
        return serverService.locate(fileNameDto);
    }

    @DeleteMapping("/bye/{owner}")
    public void bye(@PathVariable("owner") String owner) {
        serverService.bye(owner);
    }

    @DeleteMapping("/all")
    public void deleteAll(){
        serverService.deleteAll();
    }
}
