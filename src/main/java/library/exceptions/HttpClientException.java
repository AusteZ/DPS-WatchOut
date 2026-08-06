package library.exceptions;

public class HttpClientException extends RuntimeException {
    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(String message) {
        super(message);
    }

    public static class HttpResponseException extends RuntimeException {
        public HttpResponseException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpResponseException(String message) {
            super(message);
        }
    }
}
