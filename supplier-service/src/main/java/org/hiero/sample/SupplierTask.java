package org.hiero.sample;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Hbar;
import com.openelements.hiero.base.AccountClient;
import com.openelements.hiero.base.HieroException;
import com.openelements.hiero.base.SmartContractClient;
import com.openelements.hiero.base.data.Bytes;
import com.openelements.hiero.base.data.ContractParam;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SupplierTask {

    private final static Logger log = LoggerFactory.getLogger(SupplierTask.class);

    private final static ZoneId ZONE_ID_OF_NETWORK = ZoneId.of("UTC");

    private final SmartContractClient smartContractClient;

    private final AccountClient accountClient;

    private final ContractId contractId;

    @Autowired
    public SupplierTask(final SmartContractClient smartContractClient, final AccountClient accountClient,
            @Value("${contractId}") final String contractIdValue) {
        this.smartContractClient = smartContractClient;
        this.accountClient = accountClient;
        this.contractId = ContractId.fromString(contractIdValue);
    }

    @Scheduled(fixedRate = 1_000)
    @Async
    public void uploadNewItem() {
        String identifier = UUID.randomUUID().toString();
        log.info("Uploading new item with identifier: {}", identifier);
        byte[] hash = generate32ByteHash(identifier);
        final ContractParam<String> identifierParam = ContractParam.string(identifier);
        final ContractParam<Bytes> hashParam = ContractParam.bytes32(hash);
        try {
            smartContractClient.callContractFunction(contractId, "addHash", identifierParam, hashParam);
        } catch (HieroException e) {
            log.error("Failed to upload new item", e);
        }
    }

    @Scheduled(fixedRate = 10_000)
    @Async
    public void printCurrentState() {
        try {
            final BigInteger getMissingVerificationCount = smartContractClient.callContractFunction(contractId,
                    "getMissingVerificationCount").getInt256(0);
            log.info("Missing {} Identifiers", getMissingVerificationCount);
        } catch (HieroException e) {
            log.error("Failed to get missing verification count", e);
        }
    }

    @Scheduled(fixedRate = 2_000)
    @Async
    public void printAvailableHBar() {
        try {
            final Hbar operatorAccountBalance = accountClient.getOperatorAccountBalance();
            log.info("Operator Account Balance: {}", operatorAccountBalance);
        } catch (HieroException e) {
            log.error("Failed to get missing verification count", e);
        }
    }

    private LocalDateTime getSupplyTimestamp(String identifier) {
        try {
            long timestamp = smartContractClient.callContractFunction(contractId, "getSupplyTimestamp",
                    ContractParam.string(identifier)).getInt256(0).longValue();
            final Instant instant = Instant.ofEpochSecond(timestamp);
            return LocalDateTime.ofInstant(instant, ZONE_ID_OF_NETWORK);
        } catch (HieroException e) {
            throw new RuntimeException("Failed to get supply timestamp", e);
        }
    }

    private String getMissingVerificationIdentifier(int index) {
        try {
            return smartContractClient.callContractFunction(contractId,
                    "getMissingVerificationIdentifier",
                    ContractParam.int256(BigInteger.valueOf(index))).getString(0);
        } catch (HieroException e) {
            throw new RuntimeException("Failed to get missing verification identifier", e);
        }
    }

    public static byte[] generate32ByteHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(input.getBytes());
            byte[] result = new byte[32];
            Arrays.fill(result, (byte) 0);
            System.arraycopy(hash, 0, result, 16, hash.length);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error in hashing", e);
        }
    }
}
