package com.app.ecommerce.service;

import com.app.ecommerce.model.Address;

import java.util.List;

public interface AddressService {

    List<Address> getAddressesByUserId(int userId);

    Address save(Address address);

    Address getById(int id);

    void delete(int id);
}
