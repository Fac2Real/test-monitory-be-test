# BE
팩토리얼 Backend Repository
<<<<<<< HEAD
=======


## 🛠️ 프로젝트 환경 세팅 가이드 (for Backend)

### 💻 개발 환경
- Java 17 (Amazon Corretto)
- Spring Boot 3.4.4
- Gradle
- H2 (로컬 테스트 DB)
- MQTT / Kafka / Flink 연동 예정

---

### 📦 프로젝트 초기 셋업 (처음 클론할 경우)

```bash
./gradlew clean build -x test
./gradlew bootRun
```

---
# 🛠️ Backend 프로젝트 개발 가이드

## ✅ src/main/java/com/yourproject/ 하위 폴더 구조 및 역할

| 폴더 이름     | 역할 설명 |
|--------------|-----------|
| `controller/`| REST API 엔드포인트 정의. `@RestController`, `@RequestMapping` 등을 통해 요청 처리 담당 |
| `service/`   | 비즈니스 로직 처리 계층. 컨트롤러와 레포지토리 사이에서 데이터를 가공하고 흐름 제어 |
| `repository/`| 데이터베이스 액세스를 담당하는 계층. `JpaRepository`, `CrudRepository` 등을 통해 Entity 조작 |
| `domain/`    | JPA 엔티티 클래스 정의 (DB 테이블 구조에 해당). `@Entity`, `@Table` 등 사용 |
| `dto/`       | 요청/응답에 사용되는 데이터 전송 객체. `@RequestBody`, `@ResponseBody`에 주로 사용 |
| `config/`    | 전역 설정 클래스 (MQTT, Kafka, Swagger, CORS, Security 등)를 모아두는 설정 계층 |
| `util/`      | 공통 유틸리티 클래스, 상수, 헬퍼 함수 등을 저장. 전역에서 재사용 가능한 기능들 포함 |

---

## 🔁 공통 작업 시 주의사항

### 1. `build.gradle`, `settings.gradle` 변경 시
```bash
git pull
./gradlew clean build --refresh-dependencies -x test
```

### 2. `application.properties` 변경 시
- 로컬 DB 접속 URL, 포트, 외부 연동 설정(MQTT 등) 바뀐 경우:
- `src/main/resources/application.properties` 참고
- 커밋 시 **변경 내역 주석 필수**
  - 예: `# MQTT 브로커 주소 수정 by 승희`

---

## 🔧 개발자 환경 동기화 컨벤션

| 파일 | 수정 시 규칙 |
|------|--------------|
| `application.properties` | ⚠️ 반드시 변경 주석 작성<br>예: `# MQTT 브로커 주소 수정 by 승희` |
| `build.gradle` | ⚠️ 변경 시 팀원들에게 공지 or PR 설명에 명확히 기재 |
| `application.yml` | ❌ 사용하지 않음. 모든 설정은 `.properties`로 통일 |
| `config/` 클래스 | 새로운 설정 파일 추가 시 파일명은 `XXConfig.java` 로 명명<br>예: `MqttConfig.java`, `KafkaConfig.java` |

---

## ✅ Git 커밋 메시지 컨벤션 (구분자 `|')

```
[type] | sprint | JIRA-KEY | 기능 요약 | 담당자
```

---

### 📌 예시

```
feat    | sprint0 | 없음 | 센서 등록 API 구현         | 유승희
feat    | sprint0 | IOT-123 | 센서 등록 API 구현         | 유승희
fix     | sprint1 | IOT-210 | MQTT 수신 실패 예외 처리   | 윤다인
config  | sprint0 | IOT-001 | H2 DB 설정 추가            | 김우영
docs    | sprint1 | IOT-999 | README 초안 작성           | 정민석
```

---

### ✅ 항목별 설명

| 항목       | 내용                 | 예시                        |
|------------|----------------------|-----------------------------|
| `type`     | 작업 유형            | `feat`, `fix`, `docs`, `config`, `refactor`, `test`, `chore`, `style` |
| `sprint`   | 스프린트 구분        | `sprint0`, `sprint1`, `sprint2` 등 |
| `JIRA-KEY` | 연동 이슈 번호       | `IOT-123`  , `없음`  |
| 기능 요약  | 핵심 변경 내용       | 예: `센서 등록 API 구현`   |
| 담당자     | 개발자 실명 or 닉네임 | 예: `유승희`                |

---

### ✅ 추천 커밋 예시 (복붙용)

```bash
git commit -m "feat    | sprint1 | IOT-112 | 작업자 센서 조회 API 추가 | 유승희"
git commit -m "fix     | sprint0 | IOT-009 | H2 연결 오류 수정         | 윤다인"
git commit -m "config  | sprint0 | IOT-000 | Spring Boot 3.4.4 적용    | 김우영"
git commit -m "chore   | sprint1 | IOT-999 | 커밋 컨벤션 README 정리   | 정민석"
```
>>>>>>> 594bb0e (docs | 없음 | 없음 | git commit convention | 유승희)
