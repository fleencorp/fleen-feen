package com.fleencorp.feen.repository.notification;

import com.fleencorp.feen.constant.notification.NotificationStatus;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("SELECT n FROM Notification n WHERE n.notificationId IS NOT NULL AND n.receiver = :member ORDER BY n.createdOn DESC")
  Page<Notification> findMany(@Param("member") Member member, Pageable pageable);

  @Modifying
  @Query("UPDATE Notification n SET n.notificationStatus = :status WHERE n.receiver = :receiver AND n.notificationStatus = :unreadStatus")
  void markAllAsRead(@Param("status")NotificationStatus notificationStatus, @Param("unreadStatus") NotificationStatus unreadStatus, @Param("receiver") Member member);
}
