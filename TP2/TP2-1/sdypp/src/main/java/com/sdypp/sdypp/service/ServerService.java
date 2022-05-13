package com.sdypp.sdypp.service;

import com.sdypp.sdypp.dto.FileLocationDto;
import com.sdypp.sdypp.dto.HelloDto;

public interface ServerService {

    void hello(HelloDto helloDto);

    FileLocationDto locate(String filename);

    void bye(String nodeAddress);
}
