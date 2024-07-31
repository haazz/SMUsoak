package com.smusoak.restapi.services;

import com.smusoak.restapi.models.MatchingInfo;
import com.smusoak.restapi.dto.MatchingInfoDto;
import com.smusoak.restapi.repositories.MatchingInfoRepository;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingInfoRepository matchingInfoRepository;
    private final UserRepository userRepository;
    private static final Logger logger = Logger.getLogger(MatchingService.class.getName());

    @Transactional
    public void matchUsers(MatchingInfoDto.MatchingRequest matchingRequest) {
        // 요청한 유저의 매칭 정보를 생성
        MatchingInfo myMatchingInfo = convertToEntity(matchingRequest);

        // 요청한 유저의 나이와 성별을 기준으로 매칭 후보를 검색
        List<MatchingInfo> potentialMatches = matchingInfoRepository.findPotentialMatches(
                myMatchingInfo.getUser().getUserDetail().getAge(),
                String.valueOf(myMatchingInfo.getUser().getUserDetail().getGender())
        );

        // 유효한 매칭 정보만 남기기
        potentialMatches.removeIf(otherMatchingInfo -> !isMatch(myMatchingInfo, otherMatchingInfo));

        if (potentialMatches.isEmpty()) {
            matchingInfoRepository.save(myMatchingInfo);
            logger.info("No match found, saved new matching info: " + myMatchingInfo.getUser().getMail());
        } else {
            MatchingInfo firstMatchingInfo = potentialMatches.get(0);
            matchingInfoRepository.delete(firstMatchingInfo);
            logger.info("Match found and deleted: " + firstMatchingInfo.getUser().getMail());
        }
    }

    private boolean isMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
        if (myMatchingInfo.getUser() == null || otherMatchingInfo.getUser() == null) {
            return false;
        }

        int myAge = myMatchingInfo.getUser().getUserDetail().getAge();
        int minPartnerAge = otherMatchingInfo.getMinPartnerAge();
        int maxPartnerAge = otherMatchingInfo.getMaxPartnerAge();

        // 나이 범위 확인
        boolean ageMatch = myAge >= minPartnerAge && myAge <= maxPartnerAge;
        logger.info("Age match: " + ageMatch);

        // 요청한 유저의 성별과 매칭 대상 유저의 성별 비교
        String myDesiredGender = myMatchingInfo.getPartnerGender(); // 요청한 유저가 원하는 상대방의 성별
        String targetUserGender = String.valueOf(otherMatchingInfo.getUser().getUserDetail().getGender()); // 매칭 대상 유저의 성별
        boolean genderMatchForRequester = myDesiredGender.equals(targetUserGender);
        logger.info("Gender match for requester: " + genderMatchForRequester);

        // 매칭 대상 유저의 원하는 상대방 성별과 요청한 유저의 성별 비교
        String targetDesiredGender = otherMatchingInfo.getPartnerGender(); // 매칭 대상 유저가 원하는 상대방의 성별
        String myGender = String.valueOf(myMatchingInfo.getUser().getUserDetail().getGender()); // 요청한 유저의 성별
        boolean genderMatchForTarget = targetDesiredGender.equals(myGender);
        logger.info("Gender match for target: " + genderMatchForTarget);

        // 모든 조건이 만족될 때 매칭으로 간주
        return ageMatch && genderMatchForRequester && genderMatchForTarget;
    }

    private MatchingInfo convertToEntity(MatchingInfoDto.MatchingRequest matchingRequest) {
        MatchingInfo matchingInfo = new MatchingInfo();
        matchingInfo.setMinPartnerAge(matchingRequest.getMinPartnerAge());
        matchingInfo.setMaxPartnerAge(matchingRequest.getMaxPartnerAge());
        matchingInfo.setPartnerGender(String.valueOf(matchingRequest.getPartnerGender().charAt(0)));

        Optional<User> userOptional = userRepository.findByMail(matchingRequest.getUserMail());
        if (userOptional.isPresent()) {
            matchingInfo.setUser(userOptional.get());
        } else {
            throw new IllegalArgumentException("User not found with email: " + matchingRequest.getUserMail());
        }

        // 한 명의 유저가 중복된 매칭 요청을 하지 않도록 함
        if (matchingInfoRepository.findByUserMail(matchingRequest.getUserMail()).isPresent()) {
            throw new IllegalArgumentException("이미 매칭을 시도한 유저입니다: " + matchingRequest.getUserMail());
        }// 이미 매칭을 수행하셨습니다. 또 매칭을 올리시겠습니까?

        return matchingInfo;
    }
}














//package com.smusoak.restapi.services;
//
//import com.smusoak.restapi.models.*;
//import com.smusoak.restapi.dto.*;
//import com.smusoak.restapi.repositories.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class MatchingService {
//    private final UserService userService;
//    private final MatchingInfoRepository matchingInfoRepository;
//    private final UserRepository userRepository;
//    private static final Logger logger = Logger.getLogger(MatchingService.class.getName());
//
//    @Transactional
//    public void matchUsers(MatchingInfoDto.MatchingRequest matchingRequest) {
//        // 요청한 유저(a)의 매칭 정보를 생성
//        MatchingInfo myMatchingInfo = convertToEntity(matchingRequest);
//
//        // 요청한 유저의 나이와 성별을 기준으로 매칭 후보 필터링
//        List<MatchingInfo> potentialMatches = matchingInfoRepository.findPotentialMatches(
//                myMatchingInfo.getUser().getUserDetail().getAge(),
//                myMatchingInfo.getPartnerGender()
//        );
//
//        // 유효한 매칭 정보만 남기기
//        potentialMatches.removeIf(otherMatchingInfo -> !isMatch(myMatchingInfo, otherMatchingInfo));
//
//        if (potentialMatches.isEmpty()) {
//            matchingInfoRepository.save(myMatchingInfo);
//            logger.info("No match found, saved new matching info: " + myMatchingInfo.getUser().getMail());
//        } else {
//            MatchingInfo firstMatchingInfo = potentialMatches.get(0);
//            matchingInfoRepository.delete(firstMatchingInfo);
//            logger.info("Match found and deleted: " + firstMatchingInfo.getUser().getMail());
//        }
//    }
//
//    private boolean isMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        // 두 유저의 정보가 유효한지 확인
//        if (myMatchingInfo.getUser() == null || otherMatchingInfo.getUser() == null) {
//            return false;
//        }
//
//        int age = myMatchingInfo.getUser().getUserDetail().getAge();
//        boolean ageMatch = age >= otherMatchingInfo.getMinPartnerAge() && age <= otherMatchingInfo.getMaxPartnerAge();
//
//        // 요청한 유저가 원하는 상대방의 성별과 매칭 대상 유저의 성별이 일치해야 함
//        boolean genderMatchForRequester = myMatchingInfo.getPartnerGender().equals(String.valueOf(otherMatchingInfo.getUser().getUserDetail().getGender().charValue()));
//
//        // 매칭 대상 유저가 원하는 상대방의 성별과 요청한 유저의 성별이 일치해야 함
//        boolean genderMatchForTarget = otherMatchingInfo.getPartnerGender().equals(String.valueOf(myMatchingInfo.getUser().getUserDetail().getGender().charValue()));
//
//        return ageMatch && genderMatchForRequester && genderMatchForTarget;
//    }
//
//    private MatchingInfo convertToEntity(MatchingInfoDto.MatchingRequest matchingRequest) {
//        MatchingInfo matchingInfo = new MatchingInfo();
//        matchingInfo.setMinPartnerAge(matchingRequest.getMinPartnerAge());
//        matchingInfo.setMaxPartnerAge(matchingRequest.getMaxPartnerAge());
//        matchingInfo.setPartnerGender(String.valueOf(matchingRequest.getPartnerGender().charAt(0)));
//
//        Optional<User> userOptional = userRepository.findByMail(matchingRequest.getUserMail());
//        if (userOptional.isPresent()) {
//            matchingInfo.setUser(userOptional.get());
//        } else {
//            throw new IllegalArgumentException("User not found with email: " + matchingRequest.getUserMail());
//        }
//
//        // 한 명의 유저가 중복된 매칭 요청을 하지 않도록 합니다.
//        if (matchingInfoRepository.findByUserMail(matchingRequest.getUserMail()).isPresent()) {
//            throw new IllegalArgumentException("이미 매칭을 시도한 유저입니다: " + matchingRequest.getUserMail());
//        }
//
//        return matchingInfo;
//    }
//}





//package com.smusoak.restapi.services;
//import com.smusoak.restapi.models.*;
//import com.smusoak.restapi.dto.*;
//import com.smusoak.restapi.repositories.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class MatchingService {
//    private final UserService userService;
//    private final MatchingInfoRepository matchingInfoRepository;
//    private final UserRepository userRepository;
//    private static final Logger logger = Logger.getLogger(MatchingService.class.getName());
//
//    @Transactional
//    public void matchUsers(MatchingInfoDto.MatchingRequest matchingRequest) {
//        // 요청한 유저(a)의 매칭 정보를 생성
//        MatchingInfo myMatchingInfo = convertToEntity(matchingRequest);
//
//        // 요청한 유저(a)의 매칭 정보로부터 조건에 맞는 매칭 정보(b)를 찾음
//        List<MatchingInfo> potentialMatches = matchingInfoRepository.findPotentialMatches(
//                myMatchingInfo.getUser().getUserDetail().getAge(),
//                myMatchingInfo.getPartnerGender().toString()
//        );
//
//        // 매칭 정보(b)를 통해 유저(b)를 가져옴
//        potentialMatches.removeIf(otherMatchingInfo -> !isValidMatch(myMatchingInfo, otherMatchingInfo));
//
//        if (potentialMatches.isEmpty()) {
//            matchingInfoRepository.save(myMatchingInfo);
//            logger.info("No match found, saved new matching info: " + myMatchingInfo.getUser().getMail());
//        } else {
//            MatchingInfo firstMatchingInfo = potentialMatches.get(0);
//            matchingInfoRepository.delete(firstMatchingInfo);
//            logger.info("Match found and deleted: " + firstMatchingInfo.getUser().getMail());
//        }// removeif -> if문으로 바꾸기
//    }
//
//    private boolean isValidMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        return otherMatchingInfo.getUser() != null &&
//                isMatch(myMatchingInfo, otherMatchingInfo);
//    }
//
//    private boolean isMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        return isPreferencesMatch(myMatchingInfo, otherMatchingInfo) &&
//                isPreferencesMatch(otherMatchingInfo, myMatchingInfo);
//    }// 하나로 쭐이기 중복 제거
//
//    private boolean isPreferencesMatch(MatchingInfo fromMatchingInfo, MatchingInfo toMatchingInfo) {
//        int age = fromMatchingInfo.getUser().getUserDetail().getAge();
//        boolean match = age >= toMatchingInfo.getMinPartnerAge() && age <= toMatchingInfo.getMaxPartnerAge() &&
//                fromMatchingInfo.getPartnerGender().equals(String.valueOf(toMatchingInfo.getUser().getUserDetail().getGender().charValue()));
//        logger.info("isPreferencesMatch: " + match);
//        return match;
//    }
//
////    private boolean isPreferencesMatch(MatchingInfo fromMatchingInfo, MatchingInfo toMatchingInfo) {
////        UserDetail userDetail = fromMatchingInfo.getUser().getUserDetail();
////        boolean match = userDetail.getAge() >= toMatchingInfo.getMinPartnerAge() && userDetail.getAge() <= toMatchingInfo.getMaxPartnerAge() &&
////                fromMatchingInfo.getPartnerGender().equals(String.valueOf(toMatchingInfo.getUser().getUserDetail().getGender().charValue()));
////        logger.info("isPreferencesMatch: " + match);
////        return match;
////    } // B정보를 기준으로 매칭(하나로 통일)
//
//    private MatchingInfo convertToEntity(MatchingInfoDto.MatchingRequest matchingRequest) {
//        MatchingInfo matchingInfo = new MatchingInfo();
//        matchingInfo.setMinPartnerAge(matchingRequest.getMinPartnerAge());
//        matchingInfo.setMaxPartnerAge(matchingRequest.getMaxPartnerAge());
//        matchingInfo.setPartnerGender(String.valueOf(matchingRequest.getPartnerGender().charAt(0)));
//
//        Optional<User> userOptional = userRepository.findByMail(matchingRequest.getUserMail());
//        if (userOptional.isPresent()) {
//            matchingInfo.setUser(userOptional.get());
//        } else {
//            throw new IllegalArgumentException("User not found with email: " + matchingRequest.getUserMail());
//        }
//// 한 명의 유저가 중복된 매칭 요청을 하지 않도록 합니다.
//        if (matchingInfoRepository.findByUserMail(matchingRequest.getUserMail()).isPresent()) {
//            throw new IllegalArgumentException("이미 매칭을 시도한 유저입니다: " + matchingRequest.getUserMail());
//        }
//
//        return matchingInfo;
//    }
//}

//package com.smusoak.restapi.services;
//
//import com.smusoak.restapi.models.*;
//import com.smusoak.restapi.dto.*;
//import com.smusoak.restapi.repositories.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class MatchingService {
//    private final UserService userService;
//    private final MatchingInfoRepository matchingInfoRepository;
//    private final UserRepository userRepository;
//    private static final Logger logger = Logger.getLogger(MatchingService.class.getName());
//
//    @Transactional
//    public void matchUsers(MatchingInfoDto.MatchingRequest matchingRequest) {
//        // Convert DTO to entity
//        MatchingInfo matchingInfo = convertToEntity(matchingRequest);
//
//        // Find potential matches
//        List<MatchingInfo> matchingInfos = matchingInfoRepository.findPotentialMatches(
//                matchingInfo.getMinPartnerAge(),
//                matchingInfo.getMaxPartnerAge(),
//                matchingInfo.getPartnerGender().toString()
//        );
//
//
//        System.out.println("found: " + matchingInfos.toString());
//
//        // Print the details of each MatchingInfo
//        for (MatchingInfo info : matchingInfos) {
//            System.out.println("MatchingInfo: " + info);
//        }
//
//        // Flag to check if a match is found
//        boolean foundMatch = false;
//
//        for (MatchingInfo otherMatchingInfo : matchingInfos) {
//            if (otherMatchingInfo == null || otherMatchingInfo.getUser() == null) {
//                continue; // Skip any null entries
//            }
//
//            logger.info("Checking match between: " + matchingInfo.getUser().getMail() + " and " + otherMatchingInfo.getUser().getMail());
//
//            if (isMatch(matchingInfo, otherMatchingInfo)) {
//                // Handle successful match
//                matchingInfoRepository.delete(otherMatchingInfo);
//                logger.info("Match found and deleted: " + otherMatchingInfo.getUser().getMail());
//                foundMatch = true;
//                break;
//            }
//        }
//
//        if (!foundMatch) {
//            // If no match found, save the new matching info
//            matchingInfoRepository.save(matchingInfo);
//            logger.info("No match found, saved new matching info: " + matchingInfo.getUser().getMail());
//        }
//    }
//
//    private boolean isMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        return isMyPreferencesMatch(myMatchingInfo, otherMatchingInfo) &&
//                isOtherPreferencesMatch(myMatchingInfo, otherMatchingInfo);
//    }
//
//    private boolean isMyPreferencesMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        if (myMatchingInfo.getUser() == null || otherMatchingInfo.getUser() == null) {
//            return false;
//        }
//        int myAge = myMatchingInfo.getUser().getUserDetail().getAge();
//        boolean match = myAge >= otherMatchingInfo.getMinPartnerAge() && myAge <= otherMatchingInfo.getMaxPartnerAge() &&
//                myMatchingInfo.getPartnerGender().equals(String.valueOf(otherMatchingInfo.getUser().getUserDetail().getGender().charValue()));
//        logger.info("isMyPreferencesMatch: " + match);
//        return match;
//    }
//
//    private boolean isOtherPreferencesMatch(MatchingInfo myMatchingInfo, MatchingInfo otherMatchingInfo) {
//        if (myMatchingInfo.getUser() == null || otherMatchingInfo.getUser() == null) {
//            return false;
//        }
//        int otherAge = otherMatchingInfo.getUser().getUserDetail().getAge();
//        boolean match = otherAge >= myMatchingInfo.getMinPartnerAge() && otherAge <= myMatchingInfo.getMaxPartnerAge() &&
//                otherMatchingInfo.getPartnerGender().equals(String.valueOf(myMatchingInfo.getUser().getUserDetail().getGender().charValue()));
//        logger.info("isOtherPreferencesMatch: " + match);
//        return match;
//    }
//
//
//    private MatchingInfo convertToEntity(MatchingInfoDto.MatchingRequest matchingRequest) {
//        MatchingInfo matchingInfo = new MatchingInfo();
//        matchingInfo.setMinPartnerAge(matchingRequest.getMinPartnerAge());
//        matchingInfo.setMaxPartnerAge(matchingRequest.getMaxPartnerAge());
//        matchingInfo.setPartnerGender(String.valueOf(matchingRequest.getPartnerGender().charAt(0)));
//
//        Optional<User> userOptional = userRepository.findByMail(matchingRequest.getUserMail());
//        if (userOptional.isPresent()) {
//            matchingInfo.setUser(userOptional.get());
//        } else {
//            throw new IllegalArgumentException("User not found with email: " + matchingRequest.getUserMail());
//        }
//
//        return matchingInfo;
//    }
//}

//    private MatchingInfoDto.MatchingRequest convertToDto(MatchingInfo matchingInfo) {
//        MatchingInfoDto.MatchingRequest dto = new MatchingInfoDto.MatchingRequest();
//        dto.setMinPartnerAge(matchingInfo.getMinPartnerAge());
//        dto.setMaxPartnerAge(matchingInfo.getMaxPartnerAge());
//        dto.setPartnerGender(matchingInfo.getPartnerGender().toString());
//        dto.setUserMail(matchingInfo.getUser().getMail());
//        return dto;
//    }







