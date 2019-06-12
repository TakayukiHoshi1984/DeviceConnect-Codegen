package org.deviceconnect.codegen.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameDuplicationCounter {

    private final Map<String, NameDuplication> duplications = new HashMap<>();

    public NameDuplicationCounter() {}

    public void count(final String name) {
        NameDuplication dup = duplications.get(name);
        if (dup == null) {
            dup = new NameDuplication(name);
            duplications.put(name, dup);
        }
        dup.countUp();
    }

    public List<NameDuplication> getDuplications() {
        List<NameDuplication> result = new ArrayList<>();
        for (Map.Entry<String, NameDuplication> entry : duplications.entrySet()) {
            NameDuplication dup = entry.getValue();
            if (dup.isDuplicated()) {
                result.add(dup);
            }
        }
        return result;
    }

}
