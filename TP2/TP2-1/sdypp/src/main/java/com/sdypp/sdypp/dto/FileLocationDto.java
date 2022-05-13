package com.sdypp.sdypp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileLocationDto {
    /**
     * The IP address of the file's owner
     */
    private String owner;
}
