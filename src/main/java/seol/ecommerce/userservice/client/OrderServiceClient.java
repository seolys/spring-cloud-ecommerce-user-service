package seol.ecommerce.userservice.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import seol.ecommerce.userservice.vo.ResponseOrder;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

	@GetMapping("/order-service/{userId}/orders")
	List<ResponseOrder> getOrders(@PathVariable("userId") String userId);

}
