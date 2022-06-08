package com.sdypp.sdypp.repository;

import com.sdypp.sdypp.domain.FileLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileLocationRepository extends CrudRepository<FileLocation, String> {

    List<FileLocation> findAll();

    List<FileLocation> findFileLocationsByFiles(String fileName);
    Optional<FileLocation> findFileLocationByFiles(String filename);

    void deleteAll();
}
