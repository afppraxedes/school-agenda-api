//package com.schoolagenda.application.web.controller;

//import com.schoolagenda.application.web.util.JWTUtils;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/debug")
//public class DebugController {
//
//    private final JWTUtils jwtUtils;
//
//    public DebugController(JWTUtils jwtUtils) {
//        this.jwtUtils = jwtUtils;
//    }
//
//    @PostMapping("/validate-token-manual")
//    public ResponseEntity<?> validateTokenManual(@RequestBody Map<String, String> request) {
//        try {
//            String token = request.get("token");
//            System.out.println("\n=== 🧪 MANUAL TOKEN VALIDATION ===");
//            System.out.println("🔐 Token: " + token);
//
//            boolean isValid = jwtUtils.validateToken(token);
//            System.out.println("✅ Token valid: " + isValid);
//
//            if (isValid) {
//                String username = jwtUtils.getUsername(token);
//                String id = jwtUtils.getClaimFromToken(token, "id", String.class);
//                String name = jwtUtils.getClaimFromToken(token, "name", String.class);
//                List<?> authorities = jwtUtils.getClaimFromToken(token, "authorities", List.class);
//
//                System.out.println("👤 Username: " + username);
//                System.out.println("🆔 ID: " + id);
//                System.out.println("📛 Name: " + name);
//                System.out.println("🔑 Authorities: " + authorities);
//
//                return ResponseEntity.ok(Map.of(
//                        "valid", true,
//                        "username", username,
//                        "id", id,
//                        "name", name,
//                        "authorities", authorities
//                ));
//            } else {
//                return ResponseEntity.ok(Map.of("valid", false));
//            }
//
//        } catch (Exception e) {
//            System.out.println("❌ Validation error: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    @GetMapping("/test-protected")
//    public ResponseEntity<?> testProtected(Authentication authentication) {
//        System.out.println("\n=== 🧪 PROTECTED ENDPOINT TEST ===");
//
//        if (authentication == null) {
//            System.out.println("❌ Authentication is NULL");
//            return ResponseEntity.status(403).body("Authentication is null");
//        }
//
//        System.out.println("✅ Authentication: " + authentication.getName());
//        System.out.println("✅ Authorities: " + authentication.getAuthorities());
//        System.out.println("✅ Authenticated: " + authentication.isAuthenticated());
//
//        return ResponseEntity.ok(Map.of(
//                "message", "Protected endpoint accessed successfully",
//                "user", authentication.getName(),
//                "authorities", authentication.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.toList())
//        ));
//    }
//}

package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.util.JwtService;
import com.schoolagenda.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final JwtService jwtService;

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            log.info("🧪 Debug - Validating token: {}...", token.substring(0, Math.min(20, token.length())));

            String username = jwtService.extractUsername(token);
            boolean isValid = username != null;

            return ResponseEntity.ok(Map.of(
                    "token", token.substring(0, Math.min(20, token.length())) + "...",
                    "username", username,
                    "valid", isValid
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(Map.of("status", "NOT_AUTHENTICATED"));
        }

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "status", "AUTHENTICATED",
                "user", user.getEmail(),
                "authorities", user.getAuthorities()
        ));
    }
}