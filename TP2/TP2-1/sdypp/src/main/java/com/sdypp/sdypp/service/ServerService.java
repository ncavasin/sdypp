package com.sdypp.sdypp.service;

import com.sdypp.sdypp.dto.FileOwnerDto;
import com.sdypp.sdypp.dto.HelloDto;

public interface ServerService {

    void hello(HelloDto helloDto);

    FileOwnerDto locate(String filename);

    void bye(String owner);
}
