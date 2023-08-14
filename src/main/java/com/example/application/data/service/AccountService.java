package com.example.application.data.service;

import com.example.application.data.model.Account;
import com.example.application.data.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void saveAccount(Account account) {
        if (account == null) {
            System.err.println("Account is null. Are you sure you have connected your form to the application?");
            return;
        }
        accountRepository.save(account);
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    public List<Account> findAllAccounts (String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return accountRepository.findAll();
        } else {
            return accountRepository.findByOwner(stringFilter);
        }
    }


}
