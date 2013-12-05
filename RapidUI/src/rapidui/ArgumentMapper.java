package rapidui;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ArgumentMapper {
	private int[] matchIndices;
	
	public ArgumentMapper(Class<?>[] srcTypes, Class<?>[] targetTypes) {
		if (!Arrays.equals(srcTypes, targetTypes)) {
			matchIndices = new int [targetTypes.length];
			for (int i = 0; i < matchIndices.length; ++i) {
				matchIndices[i] = -1;
			}
			
			for (int i = 0; i < srcTypes.length; ++i) {
				final Class<?> srcType = srcTypes[i];
				
				for (int j = 0; j < targetTypes.length; ++j) {
					if (matchIndices[j] < 0 && targetTypes[j].equals(srcType)) {
						matchIndices[j] = i;
						break;
					}
				}
			}
		}
	}
	
	public ArgumentMapper(Class<?>[] srcTypes, Method targetMethod) {
		this(srcTypes, targetMethod.getParameterTypes());
	}
	
	public Object[] match(Object... args) {
		if (matchIndices == null) {
			return args;
		} else {
			final Object[] newArgs = new Object [matchIndices.length];
			fillMatchedResult(newArgs, 0, args);
			
			return newArgs;
		}
	}
	
	public int size() {
		return matchIndices.length;
	}
	
	public boolean isMapped(int index) {
		return (matchIndices == null ? false : matchIndices[index] >= 0);
	}
	
	public void fillMatchedResult(Object[] out, int index, Object... args) {
		for (int i = 0; i < matchIndices.length; ++i) {
			final int newIndex = matchIndices[i];
			if (newIndex >= 0) {
				out[index + i] = args[newIndex];
			}
		}
	}
	
	public boolean isIdentical() {
		return matchIndices == null;
	}
}
