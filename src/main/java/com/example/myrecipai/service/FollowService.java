package com.example.myrecipai.service;

import com.example.myrecipai.exception.BadRequestException;
import com.example.myrecipai.model.Follow;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.FollowRepository;
import com.example.myrecipai.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    public FollowService(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }
    public void followUser(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("Followee not found"));
        if (followRepository.findByFollowingIdAndFollowedId(follower.getId(), followee.getId()).isPresent()){
            throw new BadRequestException("You are already following this user");
        }else if (followerId.equals(followeeId)){
            throw new BadRequestException("You can not follow yourself");
        }
        Follow follow = new Follow();
        follow.setFollowing(follower);
        follow.setFollowed(followee);
        follow.setDateCreated(new Date());

        followRepository.save(follow);
    }
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        if (followRepository.findByFollowingIdAndFollowedId(followerId, followeeId).isPresent()){
            followRepository.deleteByFollowingIdAndFollowedId(followerId, followeeId);
        }else {
            throw new BadRequestException("You are not following this user");
        }
    }

    public boolean isFollowing (Long followeeId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Follow> following = followRepository.findByFollowingIdAndFollowedId(user.getId(), followeeId);
        return following.isPresent();
    }

    public List<Follow> getAllFollowedByUser(Long userId){
        return followRepository.findAllFollowedIdByFollowingId(userId);
    }

    public List<Follow> getAllFollowersOfUser(Long userId){
        return followRepository.findAllFollowingIdByFollowedId(userId);
    }
}
