package com.ecso.project.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private int rackNo;
    private String rankTitle; 

}