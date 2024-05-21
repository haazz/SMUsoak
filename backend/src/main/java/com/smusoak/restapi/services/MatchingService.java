package com.smusoak.restapi.services;

import com.smusoak.restapi.models.MatchingInfo;
import com.smusoak.restapi.repositories.MatchingInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {
//    private final UserService userService;
//    private final MatchingInfoRepository matchingInfoRepository;
//
//    public MatchingService(UserService userService, MatchingInfoRepository matchingInfoRepository) {
//        this.userService = userService;
//        this.matchingInfoRepository = matchingInfoRepository;
//    }
//
//    public void matchUsers(MatchingInfo matchingInfo) {
//        if (matchingInfo == null || matchingInfo.getUser() == null) {
//            throw new IllegalArgumentException("MatchingInfo or its User cannot be null");
//        }
//
//        List<MatchingInfo> matchingInfos = matchingInfoRepository.findAll();
//
//        for (MatchingInfo otherMatchingInfo : matchingInfos) {
//            if (otherMatchingInfo == null || otherMatchingInfo.getUser() == null) {
//                continue; // Skip any null entries
//            }
//
//            if (isMatch(matchingInfo, otherMatchingInfo)) {
//                // 매칭이 되었을 때의 처리
//                matchingInfoRepository.delete(otherMatchingInfo);
//                return;
//            }
//        }
//
//        // 매칭이 실패한 경우의 처리
//        matchingInfoRepository.save(matchingInfo);
//    }
//
//    private boolean isMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        // 내가 원하는 조건과 상대방이 원하는 조건 간의 이중 비교
//        if (isMyPreferencesMatch(myMatchingInfo, otherMatchingInfo) &&
//                isOtherPreferencesMatch(myMatchingInfo, otherMatchingInfo)) {
//            return true;  // 이중 비교 후 두 조건이 만족할 떄 매칭 성공
//        }
//        return false;  // 하나라도 만족하지 못하면 매칭 실패
//    }
//
//    private boolean isMyPreferencesMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        // 나의 나이 및 성별이 상대방의 최소 및 최대 나이 조건과 성별과 부합하는지 확인
//        if (myMatchingInfo.getUser() == null || otherMatchingInfo.getUser() == null) {
//            return false; // If either user is null, cannot match
//        }
//
//        int myAge = myMatchingInfo.getUser().getAge();
//        return myAge >= otherMatchingInfo.getMinPartnerAge() && myAge <= otherMatchingInfo.getMaxPartnerAge() &&
//                myMatchingInfo.getPartnerGender() == otherMatchingInfo.getUser().getGender();
//    }
//
//    private boolean isOtherPreferencesMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        // 상대방의 나이 및 성별이 나의 최소 및 최대 나이 조건과 성별과 부합하는지 확인
//        if (myMatchingInfo.getUser() == null || otherMatchingInfo.getUser() == null) {
//            return false; // If either user is null, cannot match
//        }
//
//        int otherAge = otherMatchingInfo.getUser().getAge();
//        return otherAge >= myMatchingInfo.getMinPartnerAge() && otherAge <= myMatchingInfo.getMaxPartnerAge() &&
//                otherMatchingInfo.getPartnerGender() == myMatchingInfo.getUser().getGender();
//    }
}



