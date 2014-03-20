package org.openstack.atlas.service.domain.exception;

public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable th) {
        super(message, th);
    }

    public ServiceUnavailableException(Throwable th) {
        super(th);
    }

    public ServiceUnavailableException() {
        super();
    }
}
