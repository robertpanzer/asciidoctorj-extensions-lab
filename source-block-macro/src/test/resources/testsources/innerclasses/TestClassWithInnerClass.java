package testsources.innerclasses;

public class TestClassWithInnerClass {

    public void testMethod() {
        System.out.println("The outer method");
    }

    public class InnerClass {

        public void testMethod() {
            System.out.println("The inner method");
        }

        public class EvenInnerClass {

            public void testMethod() {
                System.out.println("The inner inner method");
            }
        }

    }
}