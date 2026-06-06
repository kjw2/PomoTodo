# PomoTodo Play Store Release Checklist

이 문서는 PomoTodo를 Google Play에 출시하기 전에 준비할 항목을 정리한 체크리스트입니다.

## 1. Play Console 계정

- [ ] Google Play Console 개발자 계정 생성
- [ ] 개발자 배포 계약 동의
- [ ] 1회 등록비 US$25 결제
- [ ] 개발자 신원 확인 완료
- [ ] 개인 개발자 계정이 2023-11-13 이후 생성된 계정이면 폐쇄 테스트 요구사항 확인
  - 신규 개인 계정은 프로덕션 출시 전 최소 12명 테스터가 14일 연속 opt-in 상태여야 할 수 있음

## 2. 앱 식별자와 버전

- [ ] 출시 전 `applicationId` 확정
  - 현재 값: `com.example.pomotodo`
  - 출시 후 변경하면 Google Play에서 다른 앱으로 취급되므로, 출시 전에 실제 배포용 ID로 바꾸는 것이 좋음
  - 예: `com.yourname.pomotodo`, `com.yourstudio.pomotodo`
- [ ] `versionCode` 정책 확정
  - 현재 값: `1`
  - Play 업데이트마다 반드시 증가해야 함
- [ ] `versionName` 정책 확정
  - 현재 값: `1.0`
  - 사용자에게 보이는 버전명
  - 권장 예: `1.0.0`, `1.1.0`, `1.1.1`
- [x] 앱 화면에 현재 버전 표시
  - `BuildConfig.VERSION_NAME`
  - `BuildConfig.VERSION_CODE`

## 3. 릴리즈 서명과 번들

- [ ] debug signing 제거
  - 현재 release 빌드는 테스트 편의를 위해 debug signing을 사용함
  - Play 출시 전 별도 upload keystore로 교체해야 함
- [ ] upload keystore 생성 및 안전한 위치에 백업
- [ ] Play App Signing 사용 설정
- [ ] Android App Bundle 생성
  - 권장 명령: `.\gradlew.bat bundleRelease`
  - 업로드 대상: `app/build/outputs/bundle/release/app-release.aab`
- [ ] 업로드 전 릴리즈 빌드 검증
  - `.\gradlew.bat testDebugUnitTest`
  - `.\gradlew.bat assembleRelease`
  - `.\gradlew.bat bundleRelease`

## 4. Android 정책 요구사항

- [x] Target SDK 요구사항 확인
  - 현재 `targetSdk = 36`
  - 2025-08-31부터 신규 앱과 업데이트는 Android 15/API 35 이상 타깃 필요
- [ ] 권한 사용 이유 점검
  - 현재 주요 권한: `POST_NOTIFICATIONS`
  - 사용 목적: 타이머 진행/완료 알림
- [ ] 앱이 민감 권한을 추가로 요구하지 않는지 확인
- [ ] 백그라운드 타이머/알림 동작을 실제 기기에서 검증

## 5. 개인정보와 데이터 보안

- [ ] 개인정보처리방침 URL 준비
- [ ] 앱 내부 또는 스토어 설명에서 데이터 처리 방식 명확히 설명
- [ ] Data safety 선언 작성
  - 현재 구현 기준: 할 일 데이터는 Room 로컬 DB에 저장
  - 서버 전송, 광고, 분석 SDK가 없다면 개발자가 수집/공유하는 데이터 없음에 가까움
  - 향후 광고, 분석, 백업, 로그인, 서버 동기화를 추가하면 선언을 다시 작성해야 함
- [ ] 알림 권한 설명 작성
  - 타이머 진행 상태와 완료 알림 표시 목적

## 6. 스토어 등록 정보

- [ ] 앱 이름
  - 후보: `PomoTodo`
- [ ] 짧은 설명
  - 예: `뽀모도로 타이머와 할 일을 함께 관리하는 집중 플래너`
- [ ] 긴 설명
  - 핵심 기능: 25분/50분 타이머, Todo, Room 로컬 저장, 진행 알림, 완료 알림
- [ ] 앱 카테고리
  - 권장: 생산성
- [ ] 지원 이메일
- [ ] 스토어 아이콘
- [ ] 스크린샷 최소 2장
- [ ] Feature graphic
  - 1024 x 500
  - JPG 또는 24-bit PNG
  - 알파 채널 없음
- [ ] 출시 국가 선택
- [ ] 무료/유료 여부 선택

## 7. App content 선언

- [ ] App access
  - 현재 로그인 없음
  - 리뷰어가 모든 기능에 접근 가능해야 함
- [ ] Ads
  - 현재 광고 없음
- [ ] Content rating 설문
- [ ] Target audience and content
  - 생산성 앱이므로 어린이 대상 앱으로 포지셔닝하지 않는 편이 안전함
- [ ] Data safety
- [ ] Privacy policy
- [ ] News, government, health, financial 등 특수 카테고리에 해당하지 않는지 확인

## 8. 테스트 트랙

- [ ] 내부 테스트 트랙 업로드
- [ ] 실제 기기 설치 검증
  - 앱 시작
  - 스플래시 표시
  - 25분 타이머 시작/일시정지/초기화
  - 50분 타이머 선택
  - Todo 추가/완료/삭제
  - 진행 중 알림 표시
  - 뒤로가기 후 앱이 종료되지 않고 알림 유지
  - 완료 시 알림 표시
- [ ] 폐쇄 테스트가 필요한 계정이면 테스터 목록과 14일 조건 관리
- [ ] 프로덕션 릴리즈 제출

## 9. 출시 전 현재 프로젝트의 남은 작업

- [ ] 배포용 `applicationId` 결정 및 변경
- [ ] release signing을 debug key에서 upload keystore로 변경
- [ ] `bundleRelease` 산출물 생성
- [ ] 개인정보처리방침 작성 및 호스팅
- [ ] Play Console 스토어 등록 자료 작성
- [ ] 스크린샷과 feature graphic 제작
- [ ] Play Console App content 선언 완료
