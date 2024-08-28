package wanted.ribbon.store.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    Gimbap("김밥(도시락)"), Cafe("까페"), Chinese("중국식");

    private final String name;
}
