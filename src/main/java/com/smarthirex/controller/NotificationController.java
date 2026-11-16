package com.smarthirex.controller;

import com.smarthirex.entity.Notification;
import com.smarthirex.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class NotificationController {

    @Autowired
    private NotificationService service;

    @GetMapping("/{userId}")
    public List<Notification> getAll(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @GetMapping("/unread/{userId}")
    public long getUnreadCount(@PathVariable Long userId) {
        return service.getUnreadCount(userId);
    }

    @PutMapping("/read/{id}")
    public String markRead(@PathVariable Long id) {
        service.markRead(id);
        return "Marked as read";
    }

    @PutMapping("/readall/{userId}")
    public String markAllRead(@PathVariable Long userId) {
        service.markAllRead(userId);
        return "All marked as read";
    }
}
