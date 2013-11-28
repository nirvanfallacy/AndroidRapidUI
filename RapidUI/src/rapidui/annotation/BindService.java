package rapidui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindService {
	public String action() default "";
	public String alias();
	public boolean autoCreate() default true;
	public String className() default "";
	public Class<?> classType() default Object.class;
	public String packageName() default "";
}
