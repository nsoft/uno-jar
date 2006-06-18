package com.simontuffs.onejar.services;

/**
 * A provider for IHelloService objects.  It provides a service instance
 * based on a supplied argument, in this case the language, since the service
 * just says hello, in a given language.
 * @author simon
 *
 */
public interface IHelloService {

    public IHello getService(String language);
    
}
