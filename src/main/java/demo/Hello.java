package demo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Field;

public class Hello {

    public static void main(String[] args) {
        checkUser(new User("123", "Java"));
        checkUser(new User(null, "Java"));
        checkUser(new User("123", ""));
        checkUser(new User("123", null));
    }

    private static void checkUser(User user) {
        try {
            System.out.println("Check user: " + ToStringBuilder.reflectionToString(user));
            check(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void check(User user) throws IllegalAccessException {
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ValueCheck valueCheck = field.getAnnotation(ValueCheck.class);
            if (valueCheck != null) {
                Mode mode = valueCheck.value();
                Object value = field.get(user);
                if (mode == Mode.NotNull) {
                    if (value == null) {
                        throw new IllegalArgumentException("field should not be null: " + field.getName());
                    }
                } else if (mode == Mode.NotEmpty) {
                    if (value != null) {
                        if (value instanceof String) {
                            if (((String) value).isEmpty()) {
                                throw new IllegalArgumentException("field can't be empty: " + field.getName());
                            }
                        } else {
                            throw new IllegalArgumentException("field must be a string: " + field.getName());
                        }
                    }
                }
            }
        }
    }

}

class User {
    @ValueCheck(Mode.NotNull)
    private final String id;

    @ValueCheck(Mode.NotEmpty)
    private final String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}

