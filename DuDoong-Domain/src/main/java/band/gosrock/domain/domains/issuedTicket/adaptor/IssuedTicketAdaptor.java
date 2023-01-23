package band.gosrock.domain.domains.issuedTicket.adaptor;


import band.gosrock.common.annotation.Adaptor;
import band.gosrock.domain.domains.issuedTicket.domain.IssuedTicket;
import band.gosrock.domain.domains.issuedTicket.dto.condtion.IssuedTicketCondition;
import band.gosrock.domain.domains.issuedTicket.exception.IssuedTicketNotFoundException;
import band.gosrock.domain.domains.issuedTicket.exception.IssuedTicketUserNotMatchedException;
import band.gosrock.domain.domains.issuedTicket.repository.IssuedTicketRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Adaptor
@RequiredArgsConstructor
public class IssuedTicketAdaptor {

    private final IssuedTicketRepository issuedTicketRepository;

    public IssuedTicket save(IssuedTicket issuedTicket) {
        return issuedTicketRepository.save(issuedTicket);
    }

    public void saveAll(List<IssuedTicket> issuedTickets) {
        issuedTicketRepository.saveAll(issuedTickets);
    }

    public List<IssuedTicket> findAllByOrderLineId(Long orderLineId) {
        return issuedTicketRepository.findAllByOrderLineId(orderLineId);
    }

    public IssuedTicket findForUser(Long currentUserId, Long issuedTicketId) {
        IssuedTicket issuedTicket =
                issuedTicketRepository
                        .find(issuedTicketId)
                        .orElseThrow(() -> IssuedTicketNotFoundException.EXCEPTION);
        if (!Objects.equals(issuedTicket.getUser().getId(), currentUserId)) {
            throw IssuedTicketUserNotMatchedException.EXCEPTION;
        }
        return issuedTicket;
    }

    public IssuedTicket find(Long issuedTicketId) {
        return issuedTicketRepository
                .find(issuedTicketId)
                .orElseThrow(() -> IssuedTicketNotFoundException.EXCEPTION);
    }

    public Page<IssuedTicket> searchIssuedTicket(Long page, IssuedTicketCondition condition) {
        PageRequest pageRequest =
                PageRequest.of(Math.toIntExact(page), 10, Sort.by("id").descending());
        return issuedTicketRepository.searchToPage(condition, pageRequest);
    }

    public void delete(IssuedTicket issuedTicket) {
        issuedTicket.cancel();
    }

    public List<IssuedTicket> findAllByOrderUuid(String orderUuid) {
        return issuedTicketRepository.findAllByOrderUuid(orderUuid);
    }
}
