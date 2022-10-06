package com.yeoreodigm.server.repository.board;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Follow;
import com.yeoreodigm.server.domain.board.QFollow;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.board.QFollow.follow;

@Repository
@AllArgsConstructor
public class FollowRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(Follow follow) {
        em.persist(follow);
    }

    public void saveAndFlush(Follow follow) {
        save(follow);
        em.flush();
    }

    public Follow findByMembers(Member follower, Member followee) {
        try {
            return queryFactory
                    .selectFrom(follow)
                    .where(follow.follower.id.eq(follower.getId()), follow.followee.id.eq(followee.getId()))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 팔로우 정보가 둘 이상입니다.");
        }
    }

    public List<Member> findFollowerByMember(Member followee) {
        return queryFactory
                .select(follow.follower)
                .from(follow)
                .where(follow.followee.id.eq(followee.getId()))
                .orderBy(follow.id.desc())
                .fetch();
    }

    public List<Member> findFolloweeByMember(Member follower) {
        return queryFactory
                .select(follow.followee)
                .from(follow)
                .where(follow.follower.id.eq(follower.getId()))
                .orderBy(follow.id.desc())
                .fetch();
    }

    public Long countFollowerByMember(Member followee) {
        return queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.followee.id.eq(followee.getId()))
                .fetchOne();
    }

    public Long countFolloweeByMember(Member follower) {
        return queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.follower.id.eq(follower.getId()))
                .fetchOne();
    }

}
