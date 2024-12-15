package com.example.moneyminder.mapper;

import com.example.moneyminder.VMs.UserVM;
import com.example.moneyminder.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserVM toVM(User user);
}
