package pl.edu.pw.exception;

public class SubAccountNotFoundException extends RuntimeException{
    public SubAccountNotFoundException(String msg){
        super(msg);
    }
}
