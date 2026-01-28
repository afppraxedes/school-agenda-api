//package com.schoolagenda.application.web.dto.response;

//import com.schoolagenda.domain.enums.UserRole;
//import java.util.Set;
//
//public class UserResponse {
//
//    private Long id;
//    private String email;
//    private String username;
//    private String name;
//    private Set<UserRole> roles;
//    private String pushSubscription;
//    private String profileType; // "responsible", "teacher", "director"
//
//    // Constructors
//    public UserResponse() {}
//
//    public UserResponse(Long id, String email, String username, String name,
//                        Set<UserRole> roles, String pushSubscription, String profileType) {
//        this.id = id;
//        this.email = email;
//        this.username = username;
//        this.name = name;
//        this.roles = roles;
//        this.pushSubscription = pushSubscription;
//        this.profileType = profileType;
//    }
//
//    // Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//
//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public Set<UserRole> getRoles() { return roles; }
//    public void setRoles(Set<UserRole> roles) { this.roles = roles; }
//
//    public String getPushSubscription() { return pushSubscription; }
//    public void setPushSubscription(String pushSubscription) { this.pushSubscription = pushSubscription; }
//
//    public String getProfileType() { return profileType; }
//    public void setProfileType(String profileType) { this.profileType = profileType; }
//}

package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.enums.UserRole;
import lombok.*;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String name;
    private Set<UserRole> roles;
    private String pushSubscription;
    private String profileType;

    public UserResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
