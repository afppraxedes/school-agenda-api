////package com.schoolagenda.application.web.dto;
//package com.schoolagenda.application.web.dto.request;

//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class TokenRefreshRequest {
//    private String refreshToken;
//}


//package com.schoolagenda.application.web.dto;
package com.schoolagenda.application.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshRequest {
    private String refreshToken;
}