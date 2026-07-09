package administration_server.helper;

import administration_server.service.AverageCalculationService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

public final class BinderHelper {
    public static ResourceConfig createConfig(){
        return new ResourceConfig()
                .packages("administration_server.Services")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(new AverageCalculationService()).to(AverageCalculationService.class);
                        bind(AverageCalculationService.class).to(AverageCalculationService.class);
                    }
                })
                .property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }
}
