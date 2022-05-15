package com.sdypp.sdypp.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@Builder
@RedisHash("file_location")
public class FileLocation {
    @Id
    private String owner;
    private List<String> files;
}
