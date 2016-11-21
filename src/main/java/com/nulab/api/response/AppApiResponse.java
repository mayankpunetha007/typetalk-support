package com.nulab.api.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayan on 11/20/2016.
 */
public class AppApiResponse<T> implements Serializable{

    private boolean errors = false;

    private List<String> errorMessages = new ArrayList<String>();

    private T response;

    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errors =  true;
        this.errorMessages = errorMessages;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
