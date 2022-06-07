package com.sdypp.sdypp.service;

import com.sdypp.sdypp.domain.FileLocation;
import com.sdypp.sdypp.dto.FileNameDto;
import com.sdypp.sdypp.dto.FileOwnerDto;
import com.sdypp.sdypp.dto.HelloDto;
import com.sdypp.sdypp.exceptions.BadRequestException;
import com.sdypp.sdypp.repository.FileLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {
    private final FileLocationRepository fileLocationRepository;

    @Override
    public List<FileLocation> findAll() {
        return fileLocationRepository.findAll();
    }

    @Override
    public void hello(HelloDto helloDto) {
        // If already exists, delete it
        if (fileLocationRepository.existsById(helloDto.getOwner()))
            fileLocationRepository.delete(fileLocationRepository.findById(helloDto.getOwner()).get());

        // Then save the new one
        try {
            fileLocationRepository.save(FileLocation.builder()
                    .owner(helloDto.getOwner())
                    .files(helloDto.getFiles())
                    .build());
        } catch (Exception e) {
            throw new BadRequestException("Error saving file");
        }
    }

    @Override
    public List<FileOwnerDto> locate(FileNameDto fileNameDto) {
        // Ask redis for filename
        List<FileLocation> fileLocation = fileLocationRepository.findFileLocationsByFiles(fileNameDto.getFile());
        // Return the owner
        return fileLocation.stream()
                .map(fl -> FileOwnerDto.builder()
                        .owner(fl.getOwner())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void bye(String owner) {
        // Remove entry from redis
        fileLocationRepository.deleteById(owner);
    }

    @Override
    public void deleteAll() {
        fileLocationRepository.deleteAll();
    }
}
