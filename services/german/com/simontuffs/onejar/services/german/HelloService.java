package com.simontuffs.onejar.services.german;

import com.simontuffs.onejar.services.IHello;
import com.simontuffs.onejar.services.IHelloService;

public class HelloService implements IHelloService {

    public IHello getService(String lang) {
        if (lang.equalsIgnoreCase("german")) {
            return new IHello() {
                public String sayHello() {
                    return "guten tag";
                }
            };
        }
        return null;
    }

}
