package pl.edu.pw.repository;

import pl.edu.pw.domain.Account;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class LazyLoadAccountRepositoryImpl implements LazyLoadAccountRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Account getAccountLazy(String accountNumber) {
        return (Account) entityManager.createQuery("FROM Account a LEFT JOIN FETCH a.accountHashList WHERE a.accountNumber = :accountNumber")
                .setParameter("accountNumber", accountNumber)
                .getSingleResult();
    }
}
