package org.padan.Model.Repository;

import org.padan.Model.Objects.UserDTO;

import java.util.List;

public class UserRepository implements Repository<UserDTO> {
    @Override
    public void add(UserDTO obj) {

    }

    @Override
    public void remove(UserDTO obj) {

    }

    @Override
    public void find(UserDTO obj) {

    }

    @Override
    public List<UserDTO> findAll() {
        return List.of();
    }

    @Override
    public int getSize() {
        return 0;
    }
}
