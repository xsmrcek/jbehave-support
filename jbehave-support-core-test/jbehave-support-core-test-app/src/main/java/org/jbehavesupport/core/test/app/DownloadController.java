package org.jbehavesupport.core.test.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Controller
public class DownloadController {
    @GetMapping("/download/file")
    public ResponseEntity<Object> file(@RequestParam String name) throws IOException {
        File file;
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("files/" + name);
        if (resource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message.builder().code(404).stringMessage("File not found!").build());
        } else {
            file = new File(resource.getFile());
        }

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_PDF)
            .body(Files.readAllBytes(Paths.get(file.getPath())));
    }
}
