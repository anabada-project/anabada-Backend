package com.example.anabadabackend.global.global.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PresignedUrlRequest {
    private List<String> filenames;      // ["photo1.jpg", "photo2.jpg"]
    private List<String> contentTypes;   // ["image/jpeg", "image/jpeg"]
}
