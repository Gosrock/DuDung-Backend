package band.gosrock.api.common.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** HostRoles aop 를 적용하기 위해 다는 어노테이션 - 이찬진 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HostRolesAllowed {
    /**
     * 세가지 값을 가짐 "MASTER","MANAGER","GUEST" 권한 정보는
     *
     * @see HostRoleAop
     */
    String role();

    /** 이벤트 아이디의 식별자. */
    String eventIdIdentifier() default "eventId";
}
