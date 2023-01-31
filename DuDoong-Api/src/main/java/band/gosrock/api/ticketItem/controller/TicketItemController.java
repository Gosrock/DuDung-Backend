package band.gosrock.api.ticketItem.controller;


import band.gosrock.api.ticketItem.dto.request.ApplyTicketOptionRequest;
import band.gosrock.api.ticketItem.dto.request.CreateTicketItemRequest;
import band.gosrock.api.ticketItem.dto.response.ApplyTicketOptionResponse;
import band.gosrock.api.ticketItem.dto.response.CreateTicketItemResponse;
import band.gosrock.api.ticketItem.dto.response.GetTicketItemOptionResponse;
import band.gosrock.api.ticketItem.service.ApplyTicketOptionUseCase;
import band.gosrock.api.ticketItem.service.CreateTicketItemUseCase;
import band.gosrock.api.ticketItem.service.GetTicketOptionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "access-token")
@Tag(name = "티켓 상품 관련 컨트롤러")
@RestController
@RequestMapping("/v1/event/{eventId}/ticketItems")
@RequiredArgsConstructor
public class TicketItemController {

    public final CreateTicketItemUseCase createTicketItemUseCase;
    public final ApplyTicketOptionUseCase applyTicketOptionUseCase;
    public final GetTicketOptionsUseCase getTicketOptionsUseCase;

    @Operation(
            summary = "특정 이벤트에 속하는 티켓 상품을 생성합니다.",
            description = "제휴 되지 않은 회원은 티켓 가격 0으로 강제해 보내주세요!")
    @PostMapping
    public CreateTicketItemResponse createTicketItem(
            @RequestBody @Valid CreateTicketItemRequest createTicketItemRequest,
            @PathVariable Long eventId) {
        return createTicketItemUseCase.execute(createTicketItemRequest, eventId);
    }

    @Operation(summary = "옵션을 티켓상품에 적용합니다.")
    @PatchMapping("/{ticketItemId}/option")
    public ApplyTicketOptionResponse applyTicketOption(
            @RequestBody @Valid ApplyTicketOptionRequest applyTicketOptionRequest,
            @PathVariable Long eventId,
            @PathVariable Long ticketItemId) {
        return applyTicketOptionUseCase.execute(applyTicketOptionRequest, eventId, ticketItemId);
    }

    @Operation(summary = "해당 티켓상품의 옵션을 모두 조회합니다.")
    @GetMapping("/{ticketItemId}/options")
    public GetTicketItemOptionResponse getTicketItemOptions(
            @PathVariable Long eventId, @PathVariable Long ticketItemId) {
        return getTicketOptionsUseCase.execute(eventId, ticketItemId);
    }
}
