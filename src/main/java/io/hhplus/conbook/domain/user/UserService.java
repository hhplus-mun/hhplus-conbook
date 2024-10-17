package io.hhplus.conbook.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(long id) {
        return userRepository.getUserBy(id);
    }

    public User getUserByUUID(String uuid) {
        return userRepository.getUserByUUID(uuid);
    }
}
