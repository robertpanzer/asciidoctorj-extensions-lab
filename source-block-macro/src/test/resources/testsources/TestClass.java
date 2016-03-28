package testsources;

public class TestClass {

    public void testMethod2() {
        // A test method
    }

    public <T> void testMethod3(int... ints) {
        // A method with a vararg
    }

    public void testMethod3() {
        // A test method without arguments
    }

    public void testMethod3(int x) {
        // A test method with one int arg
    }

    public void testMethod3(float x) {
        // A test method with one float arg
    }

    public void testMethod3(java.util.List list) {
        // A test method with one raw List arg
    }

    public void testMethod3(java.util.Map<String, java.util.Object> list) {
        // A test method with one parameterized Map arg
    }

    public void testMethod3(float [ ]  floats) {
        // A test method with an array of floats
    }

    public void testMethod3(java.awt.Frame[][]  frames) {
        // A test method with an 2 dim array of frames
    }

    public <T> void testMethod3(T t) {
        // A method with a T
    }

    public <T> void testMethod3(T t, boolean[] b, List<Integer> l, int i) {
        // A method with a T
    }

}