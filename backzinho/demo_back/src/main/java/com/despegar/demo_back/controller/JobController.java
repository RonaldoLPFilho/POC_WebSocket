package com.despegar.demo_back.controller;


import com.despegar.demo_back.domain.JobData;
import com.despegar.demo_back.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @MessageMapping("/get-jobs")
    @SendTo("/topic/jobs")
    public List<JobData> getJobs() {
        System.out.println("Recebida solicitação de jobs");
        List<JobData> jobs = jobService.getJobs();
        System.out.println("Retornando " + jobs.size() + " jobs");
        return jobs;
    }
}
