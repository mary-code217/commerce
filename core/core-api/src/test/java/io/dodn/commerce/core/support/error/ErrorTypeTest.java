package io.dodn.commerce.core.support.error;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorTypeTest {

    @Test
    void ErrorCode_중복_사용_확인() {
        Map<ErrorCode, Long> codeCounts = Arrays.stream(ErrorType.values())
                .collect(Collectors.groupingBy(ErrorType::getCode, Collectors.counting()));
        Set<ErrorCode> duplicates = codeCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        assertTrue(duplicates.isEmpty(), "중복된 ErrorCode가 있습니다: " + duplicates);
    }

    @Test
    void ErrorCode가_ErrorType에서_모두_사용되는지_확인() {
        Set<ErrorCode> declaredCodes = Set.of(ErrorCode.values());
        Set<ErrorCode> usedCodes = Arrays.stream(ErrorType.values())
                .map(ErrorType::getCode)
                .collect(Collectors.toSet());

        Set<ErrorCode> unused = declaredCodes.stream()
                .filter(code -> !usedCodes.contains(code))
                .collect(Collectors.toSet());

        assertTrue(unused.isEmpty(), "사용되지 않은 ErrorCode가 있습니다: " + unused);
    }
}
