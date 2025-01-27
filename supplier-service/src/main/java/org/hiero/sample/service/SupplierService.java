package org.hiero.sample.service;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import com.openelements.hiero.base.AccountClient;
import com.openelements.hiero.base.HieroException;
import com.openelements.hiero.base.SmartContractClient;
import com.openelements.hiero.base.protocol.ProtocolLayerClient;
import com.openelements.hiero.base.protocol.TransactionListener;
import com.openelements.hiero.base.protocol.TransactionType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    private final SmartContractClient smartContractClient;

    private final AccountClient accountClient;

    private final ContractId contractId;

    private AtomicLong callCount = new AtomicLong();

    @Autowired
    public SupplierService(final SmartContractClient smartContractClient, final AccountClient accountClient,
            @Value("${contractId}") final String contractIdValue, ProtocolLayerClient protocolLayerClient) {
        this.smartContractClient = smartContractClient;
        this.accountClient = accountClient;
        this.contractId = ContractId.fromString(contractIdValue);
        protocolLayerClient.addTransactionListener(new TransactionListener() {
            @Override
            public void transactionSubmitted(TransactionType transactionType, TransactionId transactionId) {
                callCount.incrementAndGet();
            }

            @Override
            public void transactionHandled(TransactionType transactionType, TransactionId transactionId,
                    Status status) {

            }
        });
    }

    public long getCallCount() {
        return callCount.get();
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
                    "getMissingVerificationCount").getUint256(0);
        } catch (HieroException e) {
            throw new RuntimeException("Failed to get missing verification count", e);
        }
    }

    public BigInteger getTotalCount() {
        try {
            return smartContractClient.callContractFunction(contractId,
                    "getTotalCount").getUint256(0);
        } catch (HieroException e) {
            throw new RuntimeException("Failed to get total count", e);
        }
    }
}
