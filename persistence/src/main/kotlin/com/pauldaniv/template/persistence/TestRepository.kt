package com.pauldaniv.template.persistence

import com.pauldaniv.template.request.TestOne

interface TestRepository {
  fun test(): TestOne
}
