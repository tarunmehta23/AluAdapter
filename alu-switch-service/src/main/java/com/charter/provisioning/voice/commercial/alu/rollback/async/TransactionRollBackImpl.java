package com.charter.provisioning.voice.commercial.alu.rollback.async;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.exceptions.NetworkException;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.handler.NetworkHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EnableAsync
@Slf4j
@Data
public class TransactionRollBackImpl  {

	private final NetworkHandler networkHandler;
	
	private List<PlexViewRequestType> requestList = new ArrayList<>();

    @Autowired
    public TransactionRollBackImpl(NetworkHandler networkService) {
        this.networkHandler = networkService;
    }

    @Async
	public void rollbackTransactions() throws ProvisioningServiceException {

		if (requestList != null) {
			requestList.forEach(plexViewRequest -> {
				try {
                    networkHandler.provisionNetwork(plexViewRequest);
				} catch (ProvisioningServiceException | NetworkException e) {
					log.error("[{}] Rollback failed the request Id ", plexViewRequest.getRequestId());
					requestList.clear();
				}
			});
			
			requestList.clear();
		}
	}

}
