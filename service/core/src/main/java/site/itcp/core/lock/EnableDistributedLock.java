package site.itcp.core.lock;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DistributedLockAutoConfiguration.class)
@Documented
public @interface EnableDistributedLock {
}
