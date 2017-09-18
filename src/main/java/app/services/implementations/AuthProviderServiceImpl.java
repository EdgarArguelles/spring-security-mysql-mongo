package app.services.implementations;

import app.models.AuthProvider;
import app.repositories.AuthProviderRepository;
import app.services.AuthProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthProviderServiceImpl implements AuthProviderService {

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Override
    public List<AuthProvider> findAll() {
        return authProviderRepository.findAll();
    }
}