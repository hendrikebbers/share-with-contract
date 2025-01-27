import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.openelements.hiero.base.config.ConsensusNode;
import com.openelements.hiero.base.config.HieroConfig;
import com.openelements.hiero.base.data.Account;
import com.openelements.hiero.base.implementation.HieroNetwork;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public class EnvBasedHieroConfig implements HieroConfig {

    private final Account operatorAccount;

    private final String networkName;

    private final List<String> mirrornodeAddresses;

    private final Set<ConsensusNode> consensusNodes;

    public EnvBasedHieroConfig() {
        final Dotenv dotenv = Dotenv.load();
        final String accountIdAsString = dotenv.get("hiero.accountId");
        final String privateKeyAsString = dotenv.get("hiero.privateKey");
        operatorAccount = Account.of(AccountId.fromString(accountIdAsString),
                PrivateKey.fromString(privateKeyAsString));
        networkName = dotenv.get("hiero.networkName");
        final String mirrornodeAddress = dotenv.get("hiero.mirrornodeAddress");
        mirrornodeAddresses = List.of(mirrornodeAddress);

        final String consensusNodeIp = dotenv.get("hiero.consensusNodeIp");
        final String consensusNodePort = dotenv.get("hiero.consensusNodePort");
        final String consensusNodeAccount = dotenv.get("hiero.consensusNodeAccount");

        final ConsensusNode consensusNode = new ConsensusNode(consensusNodeIp, consensusNodePort, consensusNodeAccount);
        consensusNodes = Set.of(consensusNode);
    }

    @Override
    public @NonNull Account getOperatorAccount() {
        return operatorAccount;
    }

    @Override
    public @NonNull Optional<String> getNetworkName() {
        return Optional.ofNullable(networkName);
    }

    @Override
    public @NonNull List<String> getMirrornodeAddresses() {
        return mirrornodeAddresses;
    }

    @Override
    public @NonNull Set<ConsensusNode> getConsensusNodes() {
        return consensusNodes;
    }

    @Override
    public @NonNull HieroNetwork getNetwork() {
        return HieroNetwork.CUSTOM;
    }
}
