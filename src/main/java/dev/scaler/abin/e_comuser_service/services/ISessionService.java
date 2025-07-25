package dev.scaler.abin.e_comuser_service.services;

import org.springframework.util.MultiValueMapAdapter;

public interface ISessionService {

    MultiValueMapAdapter<String,String> getHeaders();
}
