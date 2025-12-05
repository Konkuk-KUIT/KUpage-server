package com.kuit.kupage.common.auth.interceptor;

import com.kuit.kupage.domain.teamMatch.Part;

import java.util.List;

public record MemberParts(List<Part> memberParts) {

    public boolean contains(Part part){
        return memberParts.contains(part);
    }

}
