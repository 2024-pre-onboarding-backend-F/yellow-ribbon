package wanted.ribbon.store.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    Lunch("김밥(도시락)"), Japanese("일식"), Chinese("중국식");

    private final String name;
}
