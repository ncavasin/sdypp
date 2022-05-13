package com.sdypp.sdypp.controller;

import com.sdypp.sdypp.dto.FileOwnerDto;
import com.sdypp.sdypp.dto.HelloDto;
import com.sdypp.sdypp.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/p2p")
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;

    @PostMapping("/hello")
    public void hello(@RequestBody @Validated HelloDto helloDto) {
        serverService.hello(helloDto);
    }

    @GetMapping("bye/{owner}")
    public void bye(@PathVariable("owner") String owner) {
        serverService.bye(owner);
    }

    @GetMapping("/locate/{filename}")
    public FileOwnerDto locate(@PathVariable("filename") String filename) {
        return serverService.locate(filename);
    }
}
