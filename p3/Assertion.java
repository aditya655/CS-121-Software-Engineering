public class Assertion {
    public static ObjectAssertion assertThat(Object o) {
        return new ObjectAssertion(o);
    }

    public static StringAssertion assertThat(String s) {
        return new StringAssertion(s);
    }

    public static BooleanAssertion assertThat(boolean b) {
        return new BooleanAssertion(b);
    }

    public static IntegerAssertion assertThat(int i) {
        return new IntegerAssertion(i);
    }

    public static class ObjectAssertion {
        private final Object o;

        public ObjectAssertion(Object o) {
            this.o = o;
        }

        public ObjectAssertion isNotNull() throws Exception {
            if (o == null) {
                throw new Exception("Object is null");
            }
            return this;
        }

        public ObjectAssertion isNull() throws Exception {
            if (o != null) {
                throw new Exception("Object is not null");
            }
            return this;
        }

        public ObjectAssertion isEqualTo(Object o2) throws Exception {
            if (!o.equals(o2)) {
                throw new Exception("Objects are not equal");
            }
            return this;
        }

        public ObjectAssertion isNotEqualTo(Object o2) throws Exception {
            if (o.equals(o2)) {
                throw new Exception("Objects are equal");
            }
            return this;
        }

        public ObjectAssertion isInstanceOf(Class<?> c) throws Exception {
            if (!c.isInstance(o)) {
                throw new Exception("Object is not an instance of " + c.getName());
            }
            return this;
        }
    }

    public static class StringAssertion {
        private final String s;

        public StringAssertion(String s) {
            this.s = s;
        }

        public StringAssertion isNotNull() throws Exception {
            if (s == null) {
                throw new Exception("String is null");
            }
            return this;
        }

        public StringAssertion isNull() throws Exception {
            if (s != null) {
                throw new Exception("String is not null");
            }
            return this;
        }

        public StringAssertion isEqualTo(Object o) throws Exception {
            if (!s.equals(o)) {
                throw new Exception("Strings are not equal");
            }
            return this;
        }

        public StringAssertion isNotEqualTo(Object o) throws Exception {
            if (s.equals(o)) {
                throw new Exception("Strings are equal");
            }
            return this;
        }

        public StringAssertion startsWith(String s2) throws Exception {
            if (!s.startsWith(s2)) {
                throw new Exception("String does not start with " + s2);
            }
            return this;
        }

        public StringAssertion isEmpty() throws Exception {
            if (!s.isEmpty()) {
                throw new Exception("String is not empty");
            }
            return this;
        }

        public StringAssertion contains(String s2) throws Exception {
            if (!s.contains(s2)) {
                throw new Exception("String does not contain " + s2);
            }
            return this;
        }
    }

    public static class BooleanAssertion {
        private final boolean b;

        public BooleanAssertion(boolean b) {
            this.b = b;
        }

        public BooleanAssertion isEqualTo(boolean b2) throws Exception {
            if (b != b2) {
                throw new Exception("Booleans are not equal");
            }
            return this;
        }

        public BooleanAssertion isTrue() throws Exception {
            if (!b) {
                throw new Exception("Boolean is false");
            }
            return this;
        }

        public BooleanAssertion isFalse() throws Exception {
            if (b) {
                throw new Exception("Boolean is true");
            }
            return this;
        }
    }

    public static class IntegerAssertion {
        private final int i;

        public IntegerAssertion(int i) {
            this.i = i;
        }

        public IntegerAssertion isEqualTo(int i2) throws Exception {
            if (i != i2) {
                throw new Exception("Integers are not equal");
            }
            return this;
        }

        public IntegerAssertion isLessThan(int i2) throws Exception {
            if (i >= i2) {
                throw new Exception("Integer is not less than " + i2);
            }
            return this;
        }

        public IntegerAssertion isGreaterThan(int i2) throws Exception {
            if (i <= i2) {
                throw new Exception("Integer is not greater than " + i2);
            }
            return this;
        }
    }
}
