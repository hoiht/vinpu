package com.vinid.vinpu.web.websocket;

import static com.vinid.vinpu.config.WebsocketConfiguration.IP_ADDRESS;

import com.vinid.vinpu.service.UserService;
import com.vinid.vinpu.service.UserTrackingTimeService;
import com.vinid.vinpu.service.utils.RedisService;
import com.vinid.vinpu.web.rest.vm.RedisUser;
import com.vinid.vinpu.web.websocket.dto.ActivityDTO;

import java.security.Principal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class ActivityService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final SimpMessageSendingOperations messagingTemplate;
    
    private final UserService userService;
    
    private final UserTrackingTimeService userTrackingTimeService;

    public ActivityService(SimpMessageSendingOperations messagingTemplate, UserService userService, UserTrackingTimeService userTrackingTimeService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.userTrackingTimeService = userTrackingTimeService;
    }

    @MessageMapping("/topic/activity")
    @SendTo("/topic/tracker")
    public ActivityDTO sendActivity(@Payload ActivityDTO activityDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        activityDTO.setUserLogin(principal.getName());
        activityDTO.setSessionId(stompHeaderAccessor.getSessionId());
        activityDTO.setIpAddress(stompHeaderAccessor.getSessionAttributes().get(IP_ADDRESS).toString());
        activityDTO.setTime(Instant.now());
        log.debug("Sending user tracking data {}", activityDTO);
        return activityDTO;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        ActivityDTO activityDTO = new ActivityDTO();
        Principal principal = event.getUser();
        
        this.userService.updateStatus(principal.getName(), false);
        this.userTrackingTimeService.userTrackingTimeLogin(principal.getName());
        activityDTO.setSessionId(event.getSessionId());
        activityDTO.setPage("logout");
        messagingTemplate.convertAndSend("/topic/tracker", activityDTO);
    }
}
