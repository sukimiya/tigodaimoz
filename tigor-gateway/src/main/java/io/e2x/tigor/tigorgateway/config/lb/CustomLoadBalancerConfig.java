package io.e2x.tigor.tigorgateway.config.lb;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

public class CustomLoadBalancerConfig {

    @Bean
    public ReactorServiceInstanceLoadBalancer customLoadBalancer(Environment environment,
                                                                 LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new CustomRandomLoadBalancerClient(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class).getObject(), name);
    }

    public static class CustomRandomLoadBalancerClient implements ReactorServiceInstanceLoadBalancer {

        private final ServiceInstanceListSupplier serviceInstanceListSupplier;

        public CustomRandomLoadBalancerClient(ServiceInstanceListSupplier serviceInstanceListSupplier, String name) {
            this.serviceInstanceListSupplier = serviceInstanceListSupplier;
        }

        @Override
        public Mono<Response<ServiceInstance>> choose(Request request) {
            return serviceInstanceListSupplier.get().next().map(this::getInstanceResponse);
        }

        @Override
        public Mono<Response<ServiceInstance>> choose() {
            return ReactorServiceInstanceLoadBalancer.super.choose();
        }

        private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
            if (instances.isEmpty()) {
                return new EmptyResponse();
            }
            Random random = new Random();
            ServiceInstance instance = instances.get(random.nextInt(instances.size()));
            return new DefaultResponse(instance);
        }
    }
}