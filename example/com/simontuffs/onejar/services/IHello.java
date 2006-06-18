package com.simontuffs.onejar.services;

/**
 * A discoverable service intended to be implemented by service providers
 * declared in the META-INF/services/com.simontuffs.services.IHelloServiceProvider 
 * files (which declare service providers that can instantiate concrete
 * objects that implement this interface).
 * @author simon
 *
 */
public interface IHello {
    
    public String sayHello();

}
