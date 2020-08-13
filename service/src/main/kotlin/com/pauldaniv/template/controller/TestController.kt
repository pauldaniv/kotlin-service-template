package com.pauldaniv.template.controller

import com.pauldaniv.template.api.TestOneService
import com.pauldaniv.template.lib.second.consume.User
import com.pauldaniv.template.request.TestOne
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TestController(
    private val testOneService: TestOneService
) {
  fun getIt(): ResponseEntity<TestOne> {
    return ResponseEntity.ok(testOneService.getIt())
  }

  fun getUser(): ResponseEntity<User> {
    return ResponseEntity.ok(User("Test", "aoeu"))
  }
}
