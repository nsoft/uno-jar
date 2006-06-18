package com.simontuffs.onejar.services.french;

import com.simontuffs.onejar.services.IHello;
import com.simontuffs.onejar.services.IHelloService;

public class HelloService implements IHelloService {

    public IHello getService(String lang) {
        if (lang.equalsIgnoreCase("french")) {
            return new IHello() {
                public String sayHello() {
                    return "bonjour";
                }
            };
        }
        return null;
    }

}
