package dev.scaler.abin.e_comuser_service.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveUserRolesDto {
    private List<Long> roleIds;
}
