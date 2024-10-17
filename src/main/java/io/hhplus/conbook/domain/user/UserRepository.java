package io.hhplus.conbook.domain.user;

public interface UserRepository {

    User getUserBy(long id);

    User getUserByUUID(String uuid);
}
