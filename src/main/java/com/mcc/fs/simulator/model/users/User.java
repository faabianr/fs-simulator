package com.mcc.fs.simulator.model.users;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@ToString
@Builder
@Data
public class User {

    private int id;
    private String username;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
