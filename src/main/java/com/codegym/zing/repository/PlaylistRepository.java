package com.codegym.zing.repository;

import com.codegym.zing.model.Playlist;
import com.codegym.zing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByUser(User user);
    List<Playlist> findAllByNameContaining(String name);

}
