package com.simontuffs.onejar.services.english;

import com.simontuffs.onejar.services.IHello;
import com.simontuffs.onejar.services.IHelloService;

public class HelloService implements IHelloService {

    public IHello getService(String lang) {
        if (lang.equalsIgnoreCase("english")) {
            return new IHello() {
                public String sayHello() {
                    return "hello";
                }
            };
        }
        return null;
    }

}
