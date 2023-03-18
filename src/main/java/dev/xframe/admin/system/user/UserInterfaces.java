package dev.xframe.admin.system.user;

import dev.xframe.admin.view.values.VEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

public class UserInterfaces {

    static final TreeSet<VEnum> TypedNames = new TreeSet<>(Comparator.comparing(VEnum::getId));
    static final Map<Integer, UserInterface> TypedInterfaces = new HashMap<>();

    static {
        TypedNames.add(new VEnum(0, "Noraml"));
    }

    public static List<VEnum> get() {
        return new ArrayList<>(TypedNames);
    }
    public static void reg(int type, String name, UserInterface userInterface) {
        TypedNames.stream().filter(e -> e.getId().equals(String.valueOf(type))).findAny().ifPresent(e -> {
            throw new IllegalArgumentException(String.format("User type [key:%s, name:%s] conflict with [key:%s, name:%s]", type, name, e.getId(), e.getText()));
        });
        TypedNames.add(new VEnum(type, name));
        TypedInterfaces.put(type, userInterface);
    }

    public static class Internal {
        public static int tryValidate(String name, String pass) {
            for (Entry<Integer, UserInterface> entry : TypedInterfaces.entrySet()) {
                try {
                    entry.getValue().validate(name, pass);
                    return entry.getKey();
                } catch (Throwable ignored) {
                }
            }
            return -1;
        }
        public static UserInterface getInterface(int type) {
            return TypedInterfaces.get(type);
        }
    }
}
