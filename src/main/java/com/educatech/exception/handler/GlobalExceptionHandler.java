package com.educatech.exception.handler;

import com.educatech.dto.response.ErrorResponseDTO;
import com.educatech.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de recursos no encontrados.
     * Este método se activa cuando se lanza una de las excepciones especificadas.
     * Devuelve una respuesta HTTP 404 Not Found.
     *
     * @param ex      La excepción de recurso no encontrado lanzada.
     * @param request La petición HTTP que originó el error.
     * @return Un ResponseEntity que contiene el DTO de error y el código de estado 404.
     */
    @ExceptionHandler({
            CourseNotFoundException.class,
            UserNotFoundException.class,
            LessonNotFoundException.class,
            EnrollmentNotFoundException.class,
            TeacherNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja las excepciones relacionadas con roles incorrectos.
     * Este método se activa cuando un usuario no tiene el rol requerido para una operación.
     * Devuelve una respuesta HTTP 400 Bad Request.
     *
     * @param ex      La excepción de rol requerido lanzada.
     * @param request La petición HTTP que originó el error.
     * @return Un ResponseEntity que contiene el DTO de error y el código de estado 400.
     */
    @ExceptionHandler(TeacherRoleRequiredException.class)
    public ResponseEntity<ErrorResponseDTO> handleRoleRequiredException(TeacherRoleRequiredException ex, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Role",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador de excepciones global para cualquier otro error no controlado.
     * Esto asegura que la API nunca filtre stack traces al cliente.
     * Devuelve una respuesta HTTP 500 Internal Server Error.
     *
     * @param ex      La excepción genérica lanzada.
     * @param request La petición HTTP que originó el error.
     * @return Un ResponseEntity con un mensaje de error genérico y el código de estado 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected internal server error occurred.",
                request.getRequestURI()
        );
        // Es una buena práctica registrar el stack trace completo en el log del servidor
        // para poder depurar, pero sin exponerlo al cliente.
        // log.error("Unhandled exception:", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

