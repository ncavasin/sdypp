package com.sdypp.sdypp.repository;

import com.sdypp.sdypp.domain.FileLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileLocationRepository extends CrudRepository<FileLocation, String> {

}
