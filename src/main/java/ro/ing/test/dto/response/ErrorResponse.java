package ro.ing.test.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final int statusCode;
    private final String message;
}
