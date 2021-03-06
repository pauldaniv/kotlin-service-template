package com.pauldaniv.template.controller

import com.pauldaniv.template.api.TestOneService
import com.pauldaniv.template.lib.second.consume.User
import com.pauldaniv.template.request.TestOne
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
class TestController(private val testOneService: TestOneService) {

  @GetMapping
  fun getIt(): ResponseEntity<TestOne> {
    return ResponseEntity.ok(testOneService.getIt())
  }

  @GetMapping("/example")
  fun getExamples(): ResponseEntity<User> {
    return ResponseEntity.ok(User("Test", LocalDateTime.now().toString()))
  }
}
