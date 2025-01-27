package org.hiero.sample.service;

import com.hedera.hashgraph.sdk.ContractId;
import com.openelements.hiero.base.AccountClient;
import com.openelements.hiero.base.HieroException;
import com.openelements.hiero.base.SmartContractClient;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {
    
    private final SmartContractClient smartContractClient;

    private final AccountClient accountClient;

    private final ContractId contractId;

    @Autowired
    public SupplierService(final SmartContractClient smartContractClient, final AccountClient accountClient,
            @Value("${contractId}") final String contractIdValue) {
        this.smartContractClient = smartContractClient;
        this.accountClient = accountClient;
        this.contractId = ContractId.fromString(contractIdValue);
    }

    public BigDecimal getAccountBalance() {
        try {
            return accountClient.getOperatorAccountBalance().getValue();
        } catch (HieroException e) {
            throw new RuntimeException("Failed to get account balance", e);
        }
    }

    public BigInteger getMissingVerificationCount() {
        try {
            return smartContractClient.callContractFunction(contractId,
                    "getMissingVerificationCount").getInt256(0);
        } catch (HieroException e) {
            throw new RuntimeException("Failed to get missing verification count", e);
        }
    }
}
