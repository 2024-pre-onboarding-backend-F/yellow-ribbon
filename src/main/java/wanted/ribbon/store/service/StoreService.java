package wanted.ribbon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wanted.ribbon.store.repository.StoreRepository;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

}
