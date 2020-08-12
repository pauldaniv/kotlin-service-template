package com.pauldaniv.template.controller;

import com.pauldaniv.template.api.TestOneService;
import com.pauldaniv.template.lib.second.consume.User;
import com.pauldaniv.template.request.TestOne;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestController {
    private final TestOneService testOneService;

    ResponseEntity<TestOne> getIt() {
        return ResponseEntity.ok(testOneService.getIt());
    }

    ResponseEntity<User> getUser() {
        return ResponseEntity.ok(new User("Test", "aoeu"));
    }
}
