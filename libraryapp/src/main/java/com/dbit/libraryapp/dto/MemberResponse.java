package com.dbit.libraryapp.dto;

import com.dbit.libraryapp.model.Member;

public record MemberResponse(
        Long id, String memberCode, String name, String email, String phone, String registrationDate
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getMemberCode(), member.getName(),
                member.getEmail(), member.getPhone(), member.getRegistrationDate().toString());
    }
}
