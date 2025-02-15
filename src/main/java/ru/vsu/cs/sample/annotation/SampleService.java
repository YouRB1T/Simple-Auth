package ru.vsu.cs.sample.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vsu.cs.entities.User;

@Service
@Slf4j
@RequestMapping("/api/user")
public class SampleService {

    @Autowired
    private SampleService sampleService;

    @FullSwaggerDescription(myCustomAnnotationSummary = "set user pessword")
    @PutMapping("/register")
    public ResponseEntity<User> setPassword()
}
