package org.hiero.sample;

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

    public EnvBasedHieroConfig(@NonNull final Dotenv dotenv) {
        final String accountIdAsString = dotenv.get("OPERATOR_ACCOUNT_ID");
        final String privateKeyAsString = dotenv.get("OPERATOR_PRIVATE_KEY");
        operatorAccount = Account.of(AccountId.fromString(accountIdAsString),
                PrivateKey.fromString(privateKeyAsString));
        networkName = dotenv.get("NETWORK_NAME");
        final String mirrornodeAddress = dotenv.get("MIRROR_NODE_ADDRESS");
        mirrornodeAddresses = List.of(mirrornodeAddress);

        final String consensusNodeIp = dotenv.get("CONSENSUS_NODE_IP");
        final String consensusNodePort = dotenv.get("CONSENSUS_NODE_PORT");
        final String consensusNodeAccount = dotenv.get("CONSENSUS_NODE_ACCOUNT_ID");

        final ConsensusNode consensusNode = new ConsensusNode(consensusNodeIp, consensusNodePort, consensusNodeAccount);
        consensusNodes = Set.of(consensusNode);
    }

    public EnvBasedHieroConfig() {
        this(Dotenv.load());
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
