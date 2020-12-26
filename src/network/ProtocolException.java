package network;

public class ProtocolException extends Exception {
    private String errorMessage;
    private ErrorType error;

    public enum ErrorType { WRONG_OBJECT, SERVERSIDE_ERROR, CONNECTION_ERROR, CLOSED_CONNECTION_ERROR }

    public ProtocolException ( String errorMessage, ErrorType error){
        this.errorMessage = errorMessage;
        this.error = error;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public ErrorType getError() {
        return error;
    }
}
