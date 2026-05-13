package com.nexus.NexusShip.mapper;


import com.nexus.NexusShip.dto.request.AdminRegistrationRequest;
import com.nexus.NexusShip.dto.response.AdminResponse;
import com.nexus.NexusShip.dto.update.UserUpdateRequest;
import com.nexus.NexusShip.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdminMapper {
    //Using MapStruct instead of manual mapping

    Admin toEntity(AdminRegistrationRequest request) ;

    AdminResponse toResponse(Admin admin);

    void updateEntityFromDto(UserUpdateRequest dto , @MappingTarget Admin admin);



   /* public Admin toEntity(AdminRegistrationRequest request) {
            Admin admin = new Admin();
            admin.setFirstName(request.firstName());
            admin.setLastName(request.lastName());
            admin.setEmail(request.email());
            admin.setPassword(request.password());
            admin.setNationalId(request.nationalId());
            admin.setPhoneNumber(request.phoneNumber());
            admin.setGender(request.gender());
            admin.setSalary(request.salary());
            admin.setAdminRole(request.adminRole());

            return admin;
    }

    public AdminResponse toResponse(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getHireDate(),
                admin.getAdminRole()
        );
    }
*/


}
