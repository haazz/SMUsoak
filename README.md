# :school: SMUsoak

상명대학교 학생들을 위한 친구찾기 어플리케이션입니다.

## 🔑 핵심 기능

### 🤝 매칭
- 조건 매칭
  - 내가 원하는 상대방의 최소/최대 나이, 성별을 설정하여 매칭합니다.
  - 상대방의 정보가 내가 원하는 조건에 부합하고, 동시에 상대방이 원하는 조건과 나의 정보가 일치할 때 매칭이 성사됩니다.

### 💬 채팅
- 매칭된 사용자
  - 1 대 1 채팅 : 오픈 채팅방을 통해서 다양한 사용자와 실시간 통신을 할 수 있습니다.
  - 메시지 및 이미지 전송이 가능합니다.
- 오픈 채팅방
  - 1대1 채팅방: 제목을 설정해 1대1 채팅방을 만들면 오픈 채팅방 목록에 표시됩니다. 방에 들어오는 사람과 1대1로 대화를 나눌 수 있습니다.
  - 그룹 채팅방: 제목과 최대 인원 수를 설정해 그룹 채팅방을 개설하면 오픈 채팅방 목록에 표시됩니다. 설정한 최대 인원을 초과하지 않는 범위에서 사람들이 들어와 그룹 채팅을 진행할 수 있습니다.

### 🛎️ 알림
- 매칭 : 매칭 성공 혹은 실패시 알림을 받을 수 있습니다.
- 채팅 : 채팅 메시지에 대한 알림을 받을 수 있습니다.

## 요구사항 분석

| 요구기능 | 상세기능 | 요청번호 | 기능내용 | 비고 |
| --------| -- |---- | ---  |---- |
| 메인 | 메인화면 | HOM01 | 홈페이지의 메인 화면을 표시하여 사용자에게 주요 정보를 제공한다 |  |
| 회원 | 로그인 | USR01 | 로그인 기능 제공 |  |
|  | 회원가입 | USR02 | 회원가입 기능 제공 |  |
|  | 회원정보 수정 | USR03 | 회원정보 수정 기능 제공 |  |
| 매칭 | 매칭 등록 | MAT01 | 조건에 맞는 매칭 등록 |  |
|  | 매칭 성공 | MAT02 | 매칭 성공 후에 채팅방 자동 생성 |  |
| 채팅 | 텍스트 전송 | CHT01 | 텍스트 메시지를 전송합니다. |  |
|  | 이미지 전송 | CHT02 | 이미지를 전송합니다. |  |
|  | 수신 알림 | CHT03 | 비 접속 중인 사용자에게 알림을 전송합니다. |  |
| 오픈 채팅 | 오픈 채팅방 생성 | OPC01 | 오픈 채팅방을 개인이 생성합니다. |  |
|  | 오픈 채팅방 수정 | OPC02 | 오픈 채팅방 제목과 소개를 수정합니다. |  |
|  | 오픈 채팅방 참여 | OPC03 | 오픈 채팅방에 참여합니다. |  |
<br/>

## 시스템 아키텍쳐
<img width="528" alt="스크린샷 2024-09-12 오후 9 09 46" src="https://github.com/user-attachments/assets/c0a3a623-27af-49ba-9095-2d09d2aea2bd">


[FE]
<ul>
<li>Kotlin을 활용한 android 어플리케이션 개발</li>
</ul>
[BE]
<ul>
<li>Spring boot를 활용한 REST API 배포</li>
<li>Docker CI/CD를 활용한 test 및 배포</li>
</ul>

## ERD
