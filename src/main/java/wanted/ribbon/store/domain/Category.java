package wanted.ribbon.store.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    Lunch("김밥(도시락)"), Japanese("일식"), Chinese("중국식"),
    Cafe("까페"), Movmntcook("이동조리"), Soup("탕류"), Fastfood("패스트푸드"),
    Sashimi("생선회"), Buff("뷔페식"), Fugu("복어취급"), Pub("정종·대포집(선술집)"),
    Bsrpcook("출장조리"),Tratearm("전통찻집"),ETC("기타");

    private final String name;
}
