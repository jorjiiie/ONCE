public class test3 {
	public static void test(Object o) {
		System.out.println(o);
	}
	public static void main(String[] args) {
		Apple a = new Apple("joe");
		test((Object) a);
	}
}