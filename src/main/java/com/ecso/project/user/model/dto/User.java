package com.ecso.project.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	
    private int userNo;
    private String userName;
    private String userEmail;
    private String userPw;
    private String userPhone;
    private String userRole;
    private String enrollDate;
    private String classTerm;
    private int adoptionCount;
    private int rankNo;
    
    // 조회시 join 용도
    private String rankTitle; 

}