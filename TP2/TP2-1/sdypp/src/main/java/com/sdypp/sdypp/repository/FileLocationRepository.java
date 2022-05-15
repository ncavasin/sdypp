package com.sdypp.sdypp.repository;

import com.sdypp.sdypp.domain.FileLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileLocationRepository extends CrudRepository<FileLocation, String> {

    Optional<FileLocation> findByFileName(String fileName);

}
