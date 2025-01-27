package org.hiero.sample;

import com.hedera.hashgraph.sdk.ContractId;
import com.openelements.hiero.base.AccountClient;
import com.openelements.hiero.base.FileClient;
import com.openelements.hiero.base.HieroContext;
import com.openelements.hiero.base.SmartContractClient;
import com.openelements.hiero.base.config.HieroConfig;
import com.openelements.hiero.base.data.Account;
import com.openelements.hiero.base.data.ContractParam;
import com.openelements.hiero.base.implementation.AccountClientImpl;
import com.openelements.hiero.base.implementation.FileClientImpl;
import com.openelements.hiero.base.implementation.ProtocolLayerClientImpl;
import com.openelements.hiero.base.implementation.SmartContractClientImpl;
import com.openelements.hiero.base.protocol.ProtocolLayerClient;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class DeployContract {

    public static void main(String[] args) throws Exception {
        final Path envPath = Path.of(".env");
        final File envFile = new File(envPath.toUri());
        final Dotenv dotenv = Dotenv.configure().directory(envFile.getParent()).filename(envFile.getName()).load();
        final HieroConfig config = createHieroConfig(dotenv);
        final HieroContext hieroContext = config.createHieroContext();

        //Create accounts
        final Account supplierAccount = createAccount(hieroContext);
        final Account consumerAccount = createAccount(hieroContext);

        //Upload the contract
        final ContractId contractId = createContract(hieroContext, supplierAccount, consumerAccount);

        //Update the .env file
        updateEnv(envPath, supplierAccount, consumerAccount, contractId);
    }

    private static void updateEnv(final Path envPath, final Account supplierAccount, final Account consumerAccount,
            final ContractId contractId) throws Exception {
        final List<String> basicLines = Files.readAllLines(envPath).stream()
                .filter(line -> !line.startsWith("SUPPLIER_ACCOUNT_ID="))
                .filter(line -> !line.startsWith("SUPPLIER_PRIVATE_KEY="))
                .filter(line -> !line.startsWith("CONSUMER_ACCOUNT_ID="))
                .filter(line -> !line.startsWith("CONSUMER_PRIVATE_KEY="))
                .filter(line -> !line.startsWith("CONTRACT_ID="))
                .collect(Collectors.toUnmodifiableList());

        Files.write(envPath, basicLines, StandardOpenOption.TRUNCATE_EXISTING);

        Files.writeString(envPath, "SUPPLIER_ACCOUNT_ID=" + supplierAccount.accountId() + "\n",
                StandardOpenOption.APPEND);
        Files.writeString(envPath, "SUPPLIER_PRIVATE_KEY=" + supplierAccount.privateKey() + "\n",
                StandardOpenOption.APPEND);
        Files.writeString(envPath, "CONSUMER_ACCOUNT_ID=" + consumerAccount.accountId() + "\n",
                StandardOpenOption.APPEND);
        Files.writeString(envPath, "CONSUMER_PRIVATE_KEY=" + consumerAccount.privateKey() + "\n",
                StandardOpenOption.APPEND);
        Files.writeString(envPath, "CONTRACT_ID=" + contractId + "\n", StandardOpenOption.APPEND);
    }

    private static ContractId createContract(final HieroContext hieroContext, final Account supplierAccount,
            final Account consumerAccount)
            throws Exception {
        final ProtocolLayerClient protocolLayerClient = new ProtocolLayerClientImpl(hieroContext);
        final FileClient fileClient = new FileClientImpl(protocolLayerClient);
        final SmartContractClient client = new SmartContractClientImpl(protocolLayerClient, fileClient);
        final URL contractUrl = DeployContract.class.getClassLoader()
                .getResource("org/hiero/contracts/SimpleStorage.bin");
        final ContractParam<String> supplierAddress = ContractParam.address(supplierAccount.accountId());
        final ContractParam<String> consumerAddress = ContractParam.address(consumerAccount.accountId());
        return client.createContract(Path.of(contractUrl.toURI()), supplierAddress, consumerAddress);
    }

    private static Account createAccount(final HieroContext hieroContext)
            throws Exception {
        final ProtocolLayerClient protocolLayerClient = new ProtocolLayerClientImpl(hieroContext);
        final AccountClient accountClient = new AccountClientImpl(protocolLayerClient);
        return accountClient.createAccount(100);
    }

    private static HieroConfig createHieroConfig(final Dotenv dotenv) {
        return new EnvBasedHieroConfig(dotenv);
    }
}
