package com.sdypp.sdypp.service;

import com.sdypp.sdypp.dto.FileLocationDto;
import com.sdypp.sdypp.dto.HelloDto;
import org.springframework.stereotype.Service;

@Service
public class ServerServiceImpl implements ServerService {

    @Override
    public void hello(HelloDto helloDto) {
        // TODO

        // Parse dto and hit redis
    }

    @Override
    public FileLocationDto locate(String filename) {
        // TODO:
        // Hit redis asking for the file name
        // Parse it to a FileLocationDto and return it
        return null;
    }

    @Override
    public void bye(String nodeAddress) {
        // TODO
        // remove files from redis using nodeAddress as key
    }
}
