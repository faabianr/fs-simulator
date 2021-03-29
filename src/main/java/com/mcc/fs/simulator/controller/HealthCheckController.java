package com.mcc.fs.simulator.controller;

import com.mcc.fs.simulator.model.network.HealthCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("healthcheck")
public class HealthCheckController {

    @GetMapping
    public HealthCheckResponse healthCheck() {
        log.info("healthcheck controller called ...");
        return HealthCheckResponse.builder().message("Service running ...").build();
    }

}
