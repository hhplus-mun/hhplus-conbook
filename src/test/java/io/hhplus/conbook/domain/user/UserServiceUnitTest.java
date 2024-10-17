package io.hhplus.conbook.domain.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    // success


    // fail: not found

}