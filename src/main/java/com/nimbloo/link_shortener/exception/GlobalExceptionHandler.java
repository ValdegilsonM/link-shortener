package com.nimbloo.link_shortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura os erros de validação (ex: URL inválida, expiração no passado)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Requisição Inválida");
        return problemDetail;
    }

    // Captura os erros de regra de negócio (ex: Link não encontrado, expirado ou inativo)
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Se a mensagem for de não encontrado, mudamos o status para 404 (Not Found)
        if (ex.getMessage().contains("não encontrado")) {
            status = HttpStatus.NOT_FOUND;
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Erro na Operação");
        return problemDetail;
    }
    
    // Captura qualquer outro erro inesperado (ex: banco de dados fora do ar)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor.");
        problemDetail.setTitle("Erro Interno");
        return problemDetail;
    }
}