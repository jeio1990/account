package com.appgate.account.api;

import com.appgate.account.model.Account;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Jgutierrez on 21/02/2019.
 */
@RestController
public class Api {

    protected Logger logger = Logger.getLogger(Api.class.getName());

    private static final List<Account> accounts;

    @Autowired
    Environment env;

    static {
        accounts = new ArrayList<>();
        accounts.add(Account.builder().id(1).customerId(1).number("11111").build());
        accounts.add(Account.builder().id(2).customerId(1).number("22222").build());
        accounts.add(Account.builder().id(3).customerId(2).number("33333").build());
        accounts.add(Account.builder().id(4).customerId(3).number("44444").build());
        accounts.add(Account.builder().id(5).customerId(3).number("55555").build());
        accounts.add(Account.builder().id(6).customerId(3).number("66666").build());
    }

    @HystrixCommand(fallbackMethod = "findByNumberFallback")
    @GetMapping("/accounts/{number}")
    public Account findByNumber(@PathVariable("number") String number) {
        logger.info(String.format("Account.findByNumber(%s)", number));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return accounts.stream().filter(it -> it.getNumber().equals(number)).findFirst().get();
    }

    public Account findByNumberFallback(String number) {
        logger.info(String.format("Account.findByNumberFallback(%s)", number));
        return null;
    }

    @HystrixCommand(fallbackMethod = "findByCustomerFallback")
    @GetMapping("/accounts/customer/{customer}")
    public List<Account> findByCustomer(@PathVariable("customer") Integer customerId) {
        logger.info(String.format("Account.findByCustomer(%s)", customerId));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return accounts.stream().filter(it -> it.getCustomerId().intValue() == customerId.intValue())
                .collect(Collectors.toList());
    }

    public List<Account> findByCustomerFallback(Integer customerId) {
        logger.info(String.format("Account.findByCustomerFallback(%s)", customerId));
        return new ArrayList<>();
    }

    @HystrixCommand(fallbackMethod = "findAllFallback",
            threadPoolKey = "DomesticPOThreadPool",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "30000"),
                    @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests", value = "15"),
                    @HystrixProperty(name = "fallback.enabled", value = "true"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "90"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "50000")
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "5"),
                    @HystrixProperty(name = "maxQueueSize", value = "5")
            })
    @GetMapping("/accounts")
    public List<Account> findAll() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Account.findAll()");
        return accounts;
    }

    public List<Account> findAllFallback() {
        logger.info("Account.findAllFallback()");
        // logger.info(env.getProperty("hystrix.threadpool.default.coreSize"));
        return new ArrayList<>();
    }


}
