package com.sdypp.sdypp.p2p;

import com.sdypp.sdypp.dto.FileLocationDto;
import com.sdypp.sdypp.dto.HelloDto;
import com.sdypp.sdypp.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/api/p2p")
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;

    @PostMapping("/hello")
    public void hello(@RequestBody @Validated HelloDto helloDto) {
        serverService.hello(helloDto);
    }

    @GetMapping("bye/{nodeAddress}")
    public void bye(@PathVariable("nodeAddress") String nodeAddress) {
        serverService.bye(nodeAddress);
    }

    @GetMapping("/locate/{filename}")
    public FileLocationDto locate(@PathVariable("filename") String filename) {
        return serverService.locate(filename);
    }
}
