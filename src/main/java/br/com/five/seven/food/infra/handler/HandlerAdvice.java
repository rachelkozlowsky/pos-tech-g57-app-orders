package br.com.five.seven.food.infra.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class HandlerAdvice {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado no servidor"
        );
        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("path", request.getRequestURL().toString());
        problemDetail.setProperty("error", ex.getClass().getSimpleName());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorDetails = buildValidationErrorMessage(ex.getBindingResult().getFieldErrors());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                errorDetails
        );
        problemDetail.setTitle("Erro de Validação");
        problemDetail.setProperty("path", request.getRequestURL().toString());
        problemDetail.setProperty("error", ex.getClass().getSimpleName());

        return problemDetail;
    }

    private String buildValidationErrorMessage(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(error -> String.format("Campo '%s': %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

    }
}
