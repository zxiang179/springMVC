package ecnu.zx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zx
 * 将变量里面的实例注入进去
 */
@Target(ElementType.FIELD)
//运行时期进行全表扫描
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {

	String value() default "";
}
