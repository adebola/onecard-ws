/*+----------------------------------------------------------------------
 ||
 ||  Class User
 ||
 ||         Author:  Adebola Omoboya
 ||
 ||        Purpose:  User Entity Class
 ||
 ||  Inherits From:  None
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  User(String username, String email, String password)
 ||                  Default NoArgsConstructor
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  None
 ||
 ++-----------------------------------------------------------------------*/
package io.factorialsystems.msscapiuser.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String walletId;
    private String secret;
    private String createdDate;
    private String organizationId;
}
