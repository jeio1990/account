package com.appgate.account.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Jgutierrez on 21/02/2019.
 */
@Data
@Builder
public class Account {

    private Integer id;
    private Integer customerId;
    private String number;
}
