package com.example.storage.repository;

import com.example.storage.entity.FileData;
import com.example.storage.entity.UserData;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileData, Integer> {

    Optional<FileData> findFileDataByUserIdAndNameAndRemote(UserData userId, String fileName, boolean remote);

    List<FileData> findFileDataByUserIdAndRemoteOrderByIdDesc(UserData userId, boolean remote, PageRequest of);

    @Query(value = "select sum (f.size) from FILES f where f.user_id =?1 and f.remote = false", nativeQuery = true)
    Optional<Integer> findSumOfSize(UserData id);

}
