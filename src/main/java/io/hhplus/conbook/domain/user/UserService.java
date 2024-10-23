package io.hhplus.conbook.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUser(long id) {
        return userRepository.getUserBy(id);
    }

    public User getUserByUUID(String uuid) {
        return userRepository.getUserByUUID(uuid);
    }
}
