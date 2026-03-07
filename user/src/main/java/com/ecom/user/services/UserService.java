package com.ecom.user.services;

import com.ecom.user.dtos.AddressDTO;
import com.ecom.user.dtos.UserRequest;
import com.ecom.user.dtos.UserResponse;
import com.ecom.user.models.Address;
import com.ecom.user.models.User;
import com.ecom.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
//    public List<User> userList = new ArrayList<>();
//    private long userCount = 1L;

    public List<UserResponse> fetchAllUsers(){
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public String addUser(UserRequest userRequest){
        User user = new User();
        updateUserFromUserRequest(user,userRequest);
        userRepository.save(user);
        return "User added successfully";
    }

    public UserResponse fetchUserById(String id) {
        return  userRepository.findById(id).map(this::mapToUserResponse).orElse(null);
    }

    public boolean updateUser(String id, UserRequest user){
        return userRepository.findById(id)
                .map(existingUser-> {
                    updateUserFromUserRequest(existingUser,user);
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }
    private void updateUserFromUserRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        if(userRequest.getAddress() != null) {
            Address address = new Address();
            address.setCity(userRequest.getAddress().getCity());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setStreet(userRequest.getAddress().getStreet());
            address.setState(userRequest.getAddress().getState());
            address.setZip(userRequest.getAddress().getZip());
            user.setAddress(address);
        }
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(String.valueOf(user.getId()));
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setPhone(user.getPhone());
        if(user.getAddress() != null){
            AddressDTO address = new AddressDTO();
            address.setCity(user.getAddress().getCity());
            address.setCountry(user.getAddress().getCountry());
            address.setStreet(user.getAddress().getStreet());
            address.setZip(user.getAddress().getZip());
            address.setState(user.getAddress().getState());
            userResponse.setAddress(address);
        }
        return userResponse;
    }
}
