package wanted.ribbon.store.repository;

import wanted.ribbon.store.domain.Store;

import java.util.List;

public interface StoreRepositoryCustom {
    List<Store> findAllStores(double lat, double lon, double meterToDegree, double meterRange, String orderBy);
}
