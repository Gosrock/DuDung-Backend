package band.gosrock.domain.domains.event.repository;


import band.gosrock.domain.domains.event.domain.Event;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface EventCustomRepository {
    Slice<Event> querySliceEventByHostIdIn(List<Long> hostId, Long lastId, Pageable pageable);
}
