package com.app.ecommerce.service;

import com.app.ecommerce.model.Address;
import com.app.ecommerce.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository repo;

    @Override
    public List<Address> getAddressesByUserId(int userId) {
        return repo.findByUserId(userId);
    }

    @Override
    public Address save(Address address) {
        return repo.save(address);
    }

    @Override
    public Address getById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void delete(int id) {
        repo.deleteById(id);
    }
}
