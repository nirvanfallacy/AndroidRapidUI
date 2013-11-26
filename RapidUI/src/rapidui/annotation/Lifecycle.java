package rapidui.annotation;

public enum Lifecycle {
	CREATE(0),
	START (1),
	RESUME(2);
	
	private int value;
	
	private Lifecycle(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
