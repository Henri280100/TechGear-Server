package com.v01.techgear_server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(RedisConnectionException.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorMessage> handleRedisConnectionException(RedisConnectionException ex,
                        WebRequest request) {
                logger.error("Redis connection error: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                request.getDescription(false)));
        }

        @ExceptionHandler(UserRolesNotFoundException.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorMessage> handleUserRolesNotFoundException(UserRolesNotFoundException ex,
                        WebRequest request) {
                logger.error("Error while finding users roles: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(ex.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getDescription(false)));
        }

        @ExceptionHandler(InvalidTokenException.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorMessage> handleInvalidTokenException(InvalidTokenException ex,
                        WebRequest request) {
                logger.error("Invalid tokens: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(ex.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getDescription(false)));
        }

        @ExceptionHandler(FileUploadingException.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorMessage> handleFileUploadingException(FileUploadingException ex,
                        WebRequest request) {
                logger.error("Error while uploading file: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                request.getDescription(false)));
        }

        @ExceptionHandler(TokenNotFoundException.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorMessage> handleTokenNotFoundException(TokenNotFoundException ex,
                        WebRequest request) {
                logger.error("Invalid token: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                request.getDescription(false)));
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        @ResponseStatus(value = HttpStatus.CONFLICT)
        public ResponseEntity<ErrorMessage> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
                        WebRequest request) {
                logger.error("User already exists: {}", ex.getMessage());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(ex.getMessage(),
                                HttpStatus.CONFLICT.value(), request.getDescription(false)));
        }

        @ExceptionHandler(GenericException.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorMessage> handleGenericException(GenericException ex, WebRequest request) {
                logger.error("Generic Exception: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                request.getDescription(false)));
        }

        @ExceptionHandler(UserNotFoundException.class)
        @ResponseStatus(value = HttpStatus.NOT_FOUND)
        public ResponseEntity<ErrorMessage> handleUserNotFoundException(UserNotFoundException ex,
                        WebRequest request) {
                logger.error("User not found: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
                                                request.getDescription(false)));
        }

        /**
         * Handle resource not found exception.
         *
         * @param ex      the resource not found exception
         * @param request the web request
         * @return the response entity with error message
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(value = HttpStatus.NOT_FOUND)
        public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex,
                        WebRequest request) {
                logger.error("Resource not found: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
                                                request.getDescription(false)));
        }

        /**
         * Handles a BadRequestException by logging the error message and returning a
         * ResponseEntity
         * with the error message and the HTTP status BAD_REQUEST.
         *
         * @param ex      the BadRequestException to handle
         * @param request the WebRequest associated with the exception
         * @return a ResponseEntity with the error message and the HTTP status
         *         BAD_REQUEST
         */
        @ExceptionHandler(BadRequestException.class)
        @ResponseStatus(value = HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException ex, WebRequest request) {
                logger.error("Bad request: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST.value(),
                                                request.getDescription(false)));
        }

        /**
         * Handles general exceptions by logging the error message and returning a
         * response entity with an error message and
         * the corresponding HTTP status code.
         *
         * @param ex      the exception that occurred
         * @param request the web request
         * @return the response entity with an error message and the corresponding HTTP
         *         status code
         */
        @ExceptionHandler(Exception.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex, WebRequest request) {
                logger.error("An unexpected error occurred: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorMessage(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                request.getDescription(false)));
        }
}
