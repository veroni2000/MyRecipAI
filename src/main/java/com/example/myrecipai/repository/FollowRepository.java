package com.example.myrecipai.repository;

import com.example.myrecipai.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByFollowingIdAndFollowedId(Long followingId, Long followedId);
    Optional<Follow> findByFollowingIdAndFollowedId(Long id, Long id1);

    List<Follow> findAllFollowingIdByFollowedId(Long followedId);

    List<Follow> findAllFollowedIdByFollowingId(Long followingId);
}
