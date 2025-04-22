package cl.example.service;

import cl.example.config.JwtUtil;
import cl.example.dto.*;
import cl.example.exception.EmailAlreadyExistsException;
import cl.example.model.Phone;
import cl.example.model.User;
import cl.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${regex.email}")
    private String emailRegex;


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private JwtUtil jwtUtil;



    public UserResponse register(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("El correo ya registrado");
        }

        if (!request.getEmail().matches(emailRegex)) {
            throw new EmailAlreadyExistsException("Formato de correo inválido");
        }


        String token = jwtUtil.generateToken(request.getEmail());

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .created(now)
                .modified(now)
                .lastLogin(now)
                .token(token)
                .isActive(true)
                .build();

         User savedUser = userRepository.save(user);

        if (request.getPhones() != null) {
            var phoneEntities = request.getPhones().stream()
                    .map(p -> Phone.builder()
                            .number(p.getNumber())
                            .citycode(p.getCitycode())
                            .contrycode(p.getContrycode())
                            .user(user)
                            .build())
                    .collect(Collectors.toList());
            user.setPhones(phoneEntities);
            userRepository.save(user);
            savedUser = userRepository.save(savedUser);
        }

        return toResponse(savedUser);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phones(user.getPhones() == null ? null :
                        user.getPhones().stream()
                                .map(p -> PhoneDTO.builder()
                                        .number(p.getNumber())
                                        .citycode(p.getCitycode())
                                        .contrycode(p.getContrycode())
                                        .build())
                                .collect(Collectors.toList()))
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.isActive())
                .build();
    }
}
