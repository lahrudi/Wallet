package org.leovegas.wallet.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ErrorResponse(HttpStatus status, String message, String details,
                            @JsonSerialize(using = LocalDateSerializer.class) LocalDate timestamp) {
}