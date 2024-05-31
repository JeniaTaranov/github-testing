package utils;

//@Builder
//@Value
public abstract class ApiResponse {
    private int statusCode;

    public Boolean isStatusOk() {
        return statusCode >= 200 && statusCode <= 299;
    }
}
