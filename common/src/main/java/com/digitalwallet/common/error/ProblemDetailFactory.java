package com.digitalwallet.common.error;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public final class ProblemDetailFactory {

    private static final String TYPE_PREFIX = "https://wallet.example/problems/";

    private ProblemDetailFactory() {}

    public static ProblemDetail create(HttpStatus status, String code, String title, String detail) {
        return create(status, code, title, detail, null);
    }

    public static ProblemDetail create(HttpStatus status, String code, String title, String detail, List<FieldErrorDetail> errors) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(TYPE_PREFIX + code.toLowerCase().replace('_', '-')));
        problem.setTitle(title);
        problem.setProperty("code", code);
        problem.setProperty("traceId", currentTraceId());
        if (errors != null && !errors.isEmpty()) {
            problem.setProperty("errors", errors);
        }
        return problem;
    }

    public static String currentTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        return traceId;
    }
}
