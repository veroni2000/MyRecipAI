package com.example.myrecipai.controller;

import com.example.myrecipai.dto.FollowRequest;
import com.example.myrecipai.model.Follow;
import com.example.myrecipai.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("")
    public ResponseEntity<String> followUser(@RequestBody FollowRequest followRequest) {
        followService.followUser(followRequest.getFollowingId(), followRequest.getFollowedId());
        return ResponseEntity.ok("Successfully followed user");
    }

    @DeleteMapping("")
    public ResponseEntity<String> unfollowUser(@RequestBody FollowRequest followRequest) {
        followService.unfollowUser(followRequest.getFollowingId(), followRequest.getFollowedId());
        return ResponseEntity.ok("Successfully unfollowed user");
    }

    @GetMapping("/check")
    public Boolean checkFollowing(@RequestParam Long followedId) {
        return followService.isFollowing(followedId);
    }

    @GetMapping("/getAllFollowedBy")
    public List<Follow> getAllFollowed(@RequestParam Long userId) {
        return followService.getAllFollowedByUser(userId);
    }

    @GetMapping("/getAllFollowers")
    public List<Follow> getAllFollowers(@RequestParam Long userId) {
        return followService.getAllFollowersOfUser(userId);
    }
}
