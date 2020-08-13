package com.pauldaniv.template.client

import com.pauldaniv.template.api.TestOneService
import com.pauldaniv.template.request.TestOne
import org.springframework.stereotype.Component

@Component
class DefaultTestClient(private val testOneService: TestOneService) : TestClient {
  override fun findByIsbn(isbn: String): TestOne {
    return testOneService.getIt()
  }
}
