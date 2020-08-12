package com.pauldaniv.template.client;

import com.pauldaniv.template.api.TestOneService;
import com.pauldaniv.template.request.TestOne;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultTestClient implements TestClient {
    private final TestOneService testOneService;

    @Override
    public TestOne findByIsbn(String isbn) {
        return testOneService.getIt();
    }
}
