package band.gosrock.domain.domains.cart.domain;


import band.gosrock.common.annotation.Validator;
import band.gosrock.domain.domains.cart.exception.CartInvalidItemKindPolicyException;
import band.gosrock.domain.domains.cart.exception.CartInvalidOptionAnswerException;
import band.gosrock.domain.domains.event.domain.Event;
import band.gosrock.domain.domains.ticket_item.adaptor.OptionAdaptor;
import band.gosrock.domain.domains.ticket_item.adaptor.TicketItemAdaptor;
import band.gosrock.domain.domains.ticket_item.domain.Option;
import band.gosrock.domain.domains.ticket_item.domain.TicketItem;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@Validator
@RequiredArgsConstructor
public class CartValidator {

    private final TicketItemAdaptor itemAdaptor;

    private final OptionAdaptor optionAdaptor;

    public void validCanCreate(Cart cart) {
        validItemKindIsOneType(cart);
        validCorrectAnswer(cart);
        TicketItem item = getItem(cart);
        Event event = item.getEvent();
        validAnswerToAllQuestion(cart, item);
        validEventIsOpen(event);
        validTicketingTime(event);
        validItemStockEnough(cart, item);
    }

    /** 티켓팅을 할 수 있는 시간을 안지났는지 검증합니다. */
    public void validTicketingTime(Event event) {
        event.validTicketingTime();
    }

    /** 아이템의 재고가 충분한지 확인합니다. */
    public void validItemStockEnough(Cart cart, TicketItem item) {
        item.validEnoughQuantity(cart.getTotalQuantity());
    }

    /** 이벤트가 현재 열려있는 상태인지 확인합니다. */
    public void validEventIsOpen(Event event) {
        event.validStatusOpen();
    }

    /** 카트에 담길때 아이템이 한 종류인지 확인합니다. */
    public void validItemKindIsOneType(Cart cart) {
        List<Long> itemIds = cart.getDistinctItemIds();
        if (itemIds.size() != 1) {
            throw CartInvalidItemKindPolicyException.EXCEPTION;
        }
    }

    /** 모든 질문지 ( 옵션그룹 )에 응답했는지 검증합니다. */
    public void validAnswerToAllQuestion(Cart cart, TicketItem item) {
        List<CartLineItem> cartLineItems = cart.getCartLineItems();
        List<Long> itemsOptionGroupIds = item.getOptionGroupIds();
        cartLineItems.forEach(
                cartLineItem -> {
                    if (!Objects.equals(
                            getAnswerOptionGroupIds(cartLineItem), itemsOptionGroupIds)) {
                        throw CartInvalidOptionAnswerException.EXCEPTION;
                    }
                });
    }

    /** 옵션에 답변을 올바르게 했는지 검증합니다. */
    public void validCorrectAnswer(Cart cart) {
        List<CartLineItem> cartLineItems = cart.getCartLineItems();
        cartLineItems.forEach(
                cartLineItem -> {
                    List<CartOptionAnswer> cartOptionAnswers = cartLineItem.getCartOptionAnswers();
                    List<Option> options = getOptionsFrom(cartLineItem);
                    cartOptionAnswers.forEach(
                            cartOptionAnswer -> {
                                Long optionId = cartOptionAnswer.getOptionId();
                                findOptionFromCartOptionAnswer(options, optionId)
                                        .validCorrectAnswer(cartOptionAnswer.getAnswer());
                            });
                });
    }

    private List<Option> getOptionsFrom(CartLineItem cartLineItem) {
        return optionAdaptor.findAllByIds(cartLineItem.getAnswerOptionIds());
    }

    private Option findOptionFromCartOptionAnswer(List<Option> options, Long optionId) {
        return options.stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst()
                .orElseThrow();
    }

    private List<Long> getAnswerOptionGroupIds(CartLineItem cartLineItem) {
        List<Option> answerOptions = getOptionsFrom(cartLineItem);
        return answerOptions.stream().map(Option::getOptionGroupId).sorted().toList();
    }

    private TicketItem getItem(Cart cart) {
        return itemAdaptor.queryTicketItem(cart.getItemId());
    }
}
