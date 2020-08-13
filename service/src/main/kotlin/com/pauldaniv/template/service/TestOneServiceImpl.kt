package com.pauldaniv.template.service

import com.pauldaniv.template.api.TestOneService
import com.pauldaniv.template.lib.first.util.TestUtil
import com.pauldaniv.template.request.TestOne
import org.springframework.stereotype.Service

@Service
class TestOneServiceImpl : TestOneService {
  override fun getIt(): TestOne {
    return TestOne(TestUtil.join("first", "second"))
  }
}
