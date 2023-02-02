package band.gosrock.domain.domains.host.repository;

import band.gosrock.domain.common.util.QueryDslUtil;
import band.gosrock.domain.domains.host.domain.Host;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static band.gosrock.domain.domains.host.domain.QHost.host;
import static band.gosrock.domain.domains.host.domain.QHostUser.hostUser;

@RequiredArgsConstructor
public class HostCustomRepositoryImpl implements HostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Host> querySliceHostsByUserId(Long userId, Long lastId, Pageable pageable) {
        OrderSpecifier[] orders = QueryDslUtil.getOrderSpecifiers(Host.class, pageable);
        List<Host> comments =
                queryFactory
                        .select(host)
                        .from(host, hostUser)
                        .where(
                                hostUser.userId.eq(userId),
                                host.hostUsers.contains(hostUser),
                                lastIdLessThan(lastId))
                        .orderBy(orders)
                        .limit(pageable.getPageSize() + 1)
                        .fetch();

        return checkLastPage(comments, pageable);
    }

    private BooleanExpression lastIdLessThan(Long lastId) {
        return lastId == null ? null : host.id.lt(lastId);
    }

    private Slice<Host> checkLastPage(List<Host> hosts, Pageable pageable) {
        boolean hasNext = false;
        if (hosts.size() > pageable.getPageSize()) {
            hasNext = true;
            hosts.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(hosts, pageable, hasNext);
    }
}
