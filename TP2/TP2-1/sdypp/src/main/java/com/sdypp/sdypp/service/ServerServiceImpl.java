package com.sdypp.sdypp.service;

import com.sdypp.sdypp.domain.FileLocation;
import com.sdypp.sdypp.dto.FileOwnerDto;
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
        // If already exists, delete it
        if (fileLocationRepository.existsById(helloDto.getOwner()))
            fileLocationRepository.delete(fileLocationRepository.findById(helloDto.getOwner()).get());
        // Then save the new one
        fileLocationRepository.save(FileLocation.builder()
                .owner(helloDto.getOwner())
                .files(helloDto.getFiles())
                .build());
    }

    @Override
    public FileOwnerDto locate(String filename) {
        // Ask redis for filename
        FileLocation fileLocation = fileLocationRepository.findByFileName(filename).orElseThrow(() -> new RuntimeException("File not found"));
        // Return the owner
        return FileOwnerDto.builder()
                .owner(fileLocation.getOwner())
                .build();
    }

    @Override
    public void bye(String owner) {
        // Remove entry from redis
        fileLocationRepository.deleteById(owner);
    }
}
