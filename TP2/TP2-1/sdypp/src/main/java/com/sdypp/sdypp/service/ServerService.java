package com.sdypp.sdypp.service;

import com.sdypp.sdypp.domain.FileLocation;
import com.sdypp.sdypp.dto.FileOwnerDto;
import com.sdypp.sdypp.dto.HelloDto;

import java.util.List;

public interface ServerService {

    List<FileLocation> findAll();

    void hello(HelloDto helloDto);

    FileOwnerDto locate(String filename);

    void bye(String owner);
}
