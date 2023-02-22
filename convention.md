**Naming Convention은 반드시 준수** (Bold 처리), 그 외에는 권장 사항으로 반드시 지키지 않아도 된다.

## Java
 - **일반 Java Naming Convention을 따름**
 - 패키지 이름: `모두 소문자. 특수문자 (언더바 포함) 금지`
 - 클래스 이름: `Camel case`
 - 등... 틀리면 IntelliJ가 Weak warning을 띄워준다.
 - `@Setter`와 `@Data`는 사용 금지. **필요한 경우 직접 생성 권장**
 - `@AllArgsConstructor`는 필드 순서가 뒤바뀌면 알아차리기 힘든 버그가 발생할 수 있으므로 직접 constructor를 생성하는 방식을 권장.
   - 또한 필드에 final을 붙일 수 있으면 붙이기. 누락된 필드 초기화를 발견할 수 있다.


## DB
 - **Table 이름**: `대문자 및 언더바(_)`

## Entity
 - **Primary Key (ID) 이름**: `[Entity명 소문자]_id`
   - User Entity -> user_id

## Dto
 - **클래스 이름**: `[DomainName]``Dto`
 - 생성자와 `@Getter` 조합 사용
 - Response dto에서는 `@RequiredArgsConstructor`, `@AllArgsConstructor` 사용을 지양하자.
   - Field 순서가 뒤바뀌었을 때 dto가 가장 버그를 발견하기 어렵다.
   - Request Dto에서는 개발자가 아니라 spring이 만들어서 자동 주입해주므로 사용해도 문제없다.

## Test
 - Unit 테스트로 구성
 - given / when / then으로 구성
 - Service 테스트는 Mockito 사용. 왠만하면 Service는 반드시 테스트.
 - Controller 테스트는 `@WebMvcTest` 사용. Service의 로직과 거의 유사하다면 굳이 하지않아도 될 것 같다.
 - Repository 테스트는 `@DataJpaTest` 사용. JpaRepository만 쓰는 경우에는, 굳이 하지 말고 새로운 메서드를 추가했을 때만 테스트 작성
 - `@Value` 는 생성자 주입 활용

## Git

### Type
 - **feat**: 기능 추가
 - **fix**: 버그 수정
 - **hotfix**: 다음 Release전에 급히 수정되어야 할 버그 수정
 - **refactor**: 코드 리팩토링
 - **test**: 테스트 추가 및 수정 (기능 추가와 함께 올라온 test는 feat으로 간주)
 - **docs**: 문서 추가 및 수정
 - **chore**: 프로젝트 설정 변경 (gradle, lombok, app 등...)
### Commit / Branch Naming Convention
 커밋 타입은 GitHub의 Label로 작성하고, 커밋 메세지만 올리기.
Branch Naming은 [커밋 타입]/[기능명]

### Branch
 - Fork된 branch에서 바로 main으로 합치지 말 것.
 - 개발 feature나 fix들은 모두 dev에서 관리하고 Release가 발생할 때마다 main에 합치기

