package band.gosrock.api.host.model.mapper;


import band.gosrock.api.host.model.dto.request.CreateHostRequest;
import band.gosrock.api.host.model.dto.request.UpdateHostRequest;
import band.gosrock.api.host.model.dto.response.HostDetailResponse;
import band.gosrock.common.annotation.Mapper;
import band.gosrock.domain.common.vo.UserInfoVo;
import band.gosrock.domain.domains.host.adaptor.HostAdaptor;
import band.gosrock.domain.domains.host.domain.Host;
import band.gosrock.domain.domains.host.domain.HostProfile;
import band.gosrock.domain.domains.host.domain.HostRole;
import band.gosrock.domain.domains.host.domain.HostUser;
import band.gosrock.domain.domains.user.adaptor.UserAdaptor;
import band.gosrock.domain.domains.user.domain.User;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@RequiredArgsConstructor
public class HostMapper {
    private final HostAdaptor hostAdaptor;
    private final UserAdaptor userAdaptor;

    public Host toEntity(CreateHostRequest createHostRequest, Long masterUserId) {
        return Host.builder()
                .name(createHostRequest.getName())
                .contactEmail(createHostRequest.getContactEmail())
                .contactNumber(createHostRequest.getContactNumber())
                .masterUserId(masterUserId)
                .build();
    }

    public HostProfile toHostProfile(UpdateHostRequest updateHostRequest) {
        return HostProfile.builder()
                .name(updateHostRequest.getName())
                .introduce(updateHostRequest.getIntroduce())
                .since(updateHostRequest.getSince())
                .profileImageUrl(updateHostRequest.getProfileImageUrl())
                .contactEmail(updateHostRequest.getContactEmail())
                .contactNumber(updateHostRequest.getContactNumber())
                .build();
    }

    public HostUser toHostUser(Long hostId, Long userId, HostRole role) {
        final Host host = hostAdaptor.findById(hostId);
        return HostUser.builder().userId(userId).host(host).role(role).build();
    }

    @Transactional(readOnly = true)
    public HostDetailResponse toHostDetailResponse(Long hostId) {
        final Host host = hostAdaptor.findById(hostId);
        return toHostDetailResponseExecute(host);
    }

    @Transactional(readOnly = true)
    public HostDetailResponse toHostDetailResponse(Host host) {
        return toHostDetailResponseExecute(host);
    }

    private HostDetailResponse toHostDetailResponseExecute(Host host) {
        Set<Long> userIdList = new HashSet<>();

        host.getHostUsers().forEach(hostUser -> userIdList.add(hostUser.getUserId()));
        final Set<UserInfoVo> userInfoVoSet =
                userAdaptor.queryUserListByIdIn(userIdList).stream()
                        .map(User::toUserInfoVo)
                        .collect(Collectors.toSet());

        return HostDetailResponse.of(host, userInfoVoSet);
    }
}
