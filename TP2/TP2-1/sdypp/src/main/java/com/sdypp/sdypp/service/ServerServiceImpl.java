package com.sdypp.sdypp.service;

import com.sdypp.sdypp.domain.FileLocation;
import com.sdypp.sdypp.dto.FileLocationDto;
import com.sdypp.sdypp.dto.HelloDto;
import com.sdypp.sdypp.repository.FileLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {
    private final FileLocationRepository fileLocationRepository;

    @Override
    public void hello(HelloDto helloDto) {
        // TODO
        if(fileLocationRepository.existsById("")){
            fileLocationRepository.save(new FileLocation());
        }


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
