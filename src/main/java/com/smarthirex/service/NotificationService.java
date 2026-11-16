package com.smarthirex.service;

import com.smarthirex.entity.Notification;
import com.smarthirex.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    public Notification send(Long userId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        return repo.save(n);
    }

    public List<Notification> getByUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return repo.countByUserIdAndIsReadFalse(userId);
    }

    public void markRead(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setRead(true);
            repo.save(n);
        });
    }

    public void markAllRead(Long userId) {
        List<Notification> list = repo.findByUserIdOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setRead(true));
        repo.saveAll(list);
    }
}
