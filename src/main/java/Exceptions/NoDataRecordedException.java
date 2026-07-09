package Exceptions;

public class NoDataRecordedException extends RuntimeException {
    public NoDataRecordedException(String message) {
        super(message);
    }
}
