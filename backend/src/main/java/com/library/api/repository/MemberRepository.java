package com.library.api.repository;

import com.library.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data-access for library members.
 *
 * @author stewicca
 * @version 1.0
 */
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByMemberNumber(String memberNumber);

    boolean existsByMemberNumber(String memberNumber);
}
