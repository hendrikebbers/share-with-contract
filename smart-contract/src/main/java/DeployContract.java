import com.openelements.hiero.base.FileClient;
import com.openelements.hiero.base.HieroContext;
import com.openelements.hiero.base.SmartContractClient;
import com.openelements.hiero.base.config.HieroConfig;
import com.openelements.hiero.base.implementation.FileClientImpl;
import com.openelements.hiero.base.implementation.ProtocolLayerClientImpl;
import com.openelements.hiero.base.implementation.SmartContractClientImpl;
import com.openelements.hiero.base.protocol.ProtocolLayerClient;
import java.net.URL;
import java.nio.file.Path;

public class DeployContract {

    public static void main(String[] args) throws Exception {
        final HieroConfig config = createHieroConfig();
        final HieroContext hieroContext = config.createHieroContext();
        final ProtocolLayerClient protocolLayerClient = new ProtocolLayerClientImpl(hieroContext);
        final FileClient fileClient = new FileClientImpl(protocolLayerClient);
        final SmartContractClient client = new SmartContractClientImpl(protocolLayerClient, fileClient);
        final URL contractUrl = DeployContract.class.getClassLoader()
                .getResource("org/hiero/contracts/SimpleStorage.bin");
        client.createContract(Path.of(contractUrl.toURI()));
    }

    private static HieroConfig createHieroConfig() {
        return new EnvBasedHieroConfig();
    }
}
