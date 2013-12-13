package rapidui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchBar {
	public int value() default 0;
	public int id() default 0;
	public String hint() default "";
	public int hintId() default 0;
}
