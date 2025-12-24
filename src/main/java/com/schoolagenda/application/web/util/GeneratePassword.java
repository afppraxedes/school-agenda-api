package com.schoolagenda.application.web.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // td3319 (Director)
        // td7120 (Teacher)
        // td8083 (Responsible)
        System.out.println(encoder.encode("td3319"));
    }

}
