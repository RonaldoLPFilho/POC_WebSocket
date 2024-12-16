package com.despegar.demo_back.service;


import com.despegar.demo_back.domain.JobData;
import com.despegar.demo_back.domain.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobService {
    private final SimpMessagingTemplate messagingTemplate;
    private List<JobData> jobs;

    @Autowired
    public JobService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        initializeJobs();
    }

    private void initializeJobs() {
        jobs = Arrays.asList(
                JobData.builder()
                        .id(1)
                        .title("tiruylo um")
                        .status(Status.PROCESSING)
                        .build(),
                JobData.builder()
                        .id(2)
                        .title("tiruylo dois")
                        .status(Status.FAILED)
                        .build()
        );
    }

    @Scheduled(fixedRate = 2000)
    public void updateJobStatus() {
        jobs = jobs.stream()
                .map(this::updateRandomStatus)
                .collect(Collectors.toList());


        messagingTemplate.convertAndSend("/topic/jobs", jobs);
        log.info("Jobs atualizados: {}", jobs); 
    }

    private JobData updateRandomStatus(JobData jobData) {
        Random random = new Random();
        int randomNumber = random.nextInt(3);

        Status newStatus = switch (randomNumber) {
            case 0 -> Status.OK;
            case 1 -> Status.FAILED;
            default -> Status.PROCESSING;
        };

        return JobData.builder()
                .id(jobData.getId())
                .title(jobData.getTitle())
                .status(newStatus)
                .build();
    }

    public List<JobData> getJobs() {
        return jobs;
    }
}
