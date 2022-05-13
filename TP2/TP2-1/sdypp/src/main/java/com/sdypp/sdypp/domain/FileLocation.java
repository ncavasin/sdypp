package com.sdypp.sdypp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash("file_location")
public class FileLocation {
    @Id
    private String owner;
    private List<String> filenames;
}
